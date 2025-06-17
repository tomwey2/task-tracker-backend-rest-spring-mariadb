# Spring Security

## Minimale Konfiguration mit fest eingebautem Test-User
Unser Ziel für diese einfache Konfiguration:

1. Wir fügen Spring Security zum Projekt hinzu.
2. Wir erstellen einen einzigen Test-Benutzer mit einem Passwort direkt im Code (in-memory), ohne die Datenbank zu berühren.
3. Wir konfigurieren die API so, dass Lesezugriffe (GET) öffentlich erlaubt sind, aber alle 
Schreibzugriffe (POST, PUT, DELETE) eine Anmeldung erfordern.
4. Wir verwenden HTTP Basic Authentication, die einfachste Form der Authentifizierung, die perfekt für 
erste Tests mit Tools wie Postman ist


### Schritt 1: Die Spring Security-Abhängigkeit hinzufügen
Öffne deine pom.xml-Datei und füge die folgende Abhängigkeit hinzu:

    <dependency>
        <groupId>org.springframework.boot</groupId>
        <artifactId>spring-boot-starter-security</artifactId>
    </dependency>

Was passiert jetzt?
Sobald du deine Anwendung mit dieser Abhängigkeit neu startest, passiert "Magie":
- Alles ist gesperrt: Spring Security sichert standardmäßig alle Endpunkte deiner Anwendung.
- 401 Unauthorized: Jeder Aufruf deiner API (z.B. in Postman) wird sofort mit dem Status 401 Unauthorized blockiert.
- Login-Formular: Wenn du einen Endpunkt im Browser aufrufst, siehst du ein von Spring generiertes Login-Formular.
Das zeigt, dass Spring Security aktiv ist. Unser nächster Schritt ist, dieses Standardverhalten nach unseren 
Wünschen zu konfigurieren.

### Schritt 2: Die Sicherheitskonfiguration erstellen
Die moderne Art, Spring Security zu konfigurieren (seit Spring Boot 3), ist über eine Konfigurationsklasse, 
die eine SecurityFilterChain-Bean definiert. Der alte WebSecurityConfigurerAdapter ist veraltet (deprecated) 
und sollte nicht mehr verwendet werden.

    @Configuration
    @EnableWebSecurity
    public class SecurityConfig {
        // Hier kommt unsere Konfiguration hinein
    }

### Schritt 3: Den PasswordEncoder und einen In-Memory-Benutzer definieren
Passwörter dürfen niemals im Klartext gespeichert werden, auch nicht für Testbenutzer im Speicher. 
Wir müssen sie verschlüsseln. Dafür definieren wir einen PasswordEncoder als Bean. Anschließend legen 
wir einen Testbenutzer an.

    @Configuration
    @EnableWebSecurity
    public class SecurityConfig {

        @Bean
        public PasswordEncoder passwordEncoder() {
            // BCrypt ist der De-facto-Standard zum Hashen von Passwörtern.
            return new BCryptPasswordEncoder();
        }

        @Bean
        public UserDetailsService userDetailsService() {
            // Wir erstellen einen Benutzer direkt im Speicher.
            // In einer echten Anwendung würden die Benutzer aus der Datenbank geladen.
            UserDetails user = User.builder()
                    .username("user")
                    .password(passwordEncoder().encode("password")) // Passwort wird verschlüsselt
                    .roles("USER") // Dem Benutzer wird die Rolle USER zugewiesen
                    .build();
        
            return new InMemoryUserDetailsManager(user);
        }
    }

### Schritt 4: Die SecurityFilterChain für die API-Regeln konfigurieren
Dies ist das Herzstück. Hier legen wir fest, wer auf welche Endpunkte zugreifen darf.

    @Configuration
    @EnableWebSecurity
    public class SecurityConfig {

    // ... passwordEncoder() und userDetailsService() von oben ...
    
        @Bean
        public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
            http
                // 1. CSRF-Schutz deaktivieren. Für zustandslose REST-APIs, die nicht
                //    von Browsern mit Sessions genutzt werden, ist dies üblich.
                .csrf(csrf -> csrf.disable())

                // 2. Die Autorisierungsregeln für HTTP-Requests definieren.
                .authorizeHttpRequests(authz -> authz
                    // Jede GET-Anfrage, die auf /api/** passt, ist für jeden erlaubt.
                    .requestMatchers(HttpMethod.GET, "/api/**").permitAll()
                    // Jede andere Anfrage an die API erfordert eine Authentifizierung.
                    .anyRequest().authenticated()
                )

                // 3. HTTP Basic Authentication aktivieren.
                .httpBasic(Customizer.withDefaults());

            return http.build();
        }
    }

## Fest-codierten In-Memory-Benutzer durch echte Benutzer aus deiner Datenbank ersetzen
### Schritt 1: Unsere User-Entity UserDetails-kompatibel machen
Spring Security arbeitet intern mit einem Interface namens UserDetails. Es definiert, welche Methoden ein 
Benutzerobjekt haben muss (z.B. getUsername(), getPassword(), getAuthorities() für die Rollen).

Wir müssen also unsere bestehende User-Klasse so anpassen, dass sie dieses Interface implementiert.

    @Data
    @Entity
    @Table(name = "app_user")
    public class User implements UserDetails { // Implementiere UserDetails
        @Id
        @GeneratedValue(strategy = GenerationType.IDENTITY)
        private Long id;
        // ...

        // --- Implementierung der UserDetails-Methoden ---

        @Override
        public Collection<? extends GrantedAuthority> getAuthorities() {
            // Wir wandeln unser einfaches 'role'-Feld in eine GrantedAuthority um.
            return List.of(new SimpleGrantedAuthority(this.role));
        }

        // getPassword() wird von Lombok @Data bereits generiert.
        // getUsername() wird von Lombok @Data bereits generiert.

        @Override
        public boolean isAccountNonExpired() {
            // Fürs Erste gehen wir davon aus, dass Konten nie ablaufen.
            return true;
        }
        // ...
    }

**Wichtige Änderungen:**
1. implements UserDetails: Unsere Klasse erfüllt jetzt den "Vertrag" von Spring Security.
2. private String role: Wir haben ein einfaches Feld für die Rolle hinzugefügt. Standardmäßig ROLE_USER.
3. getAuthorities(): Diese Methode ist entscheidend. Sie teilt Spring Security mit, welche Rollen/Berechtigungen 
der Benutzer hat. ROLE_ ist eine Standard-Präfix-Konvention.
4. Die anderen vier boolean-Methoden setzen wir für den Moment einfach auf true. Sie ermöglichen komplexere 
Szenarien wie das Sperren oder Deaktivieren von Benutzern.

### Schritt 2: Einen eigenen JpaUserDetailsService erstellen
Dieser neue Service wird das UserDetailsService-Interface implementieren und unser UserRepository nutzen, 
um Benutzer zu laden.

    @Service
    @RequiredArgsConstructor
    public class JpaUserDetailsService implements UserDetailsService {
        private final UserRepository userRepository;

        @Override
        public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
            // Wir suchen den Benutzer in unserem UserRepository.
            // Wenn er nicht gefunden wird, werfen wir die von Spring Security erwartete Exception.
            return userRepository.findByUsername(username)
                    .orElseThrow(() -> new UsernameNotFoundException("User not found with username: " + username));
        }
    }

### Schritt 3: SecurityConfig anpassen
Jetzt wird es einfach. Wir müssen nur noch unsere In-Memory-Konfiguration entfernen.

    @EnableWebSecurity
    public class SecurityConfig {
        // ...
    
        /*
        * DIESE BEAN WIRD GELÖSCHT!
        * Spring Boot findet unseren JpaUserDetailsService automatisch, weil er mit @Service
        * annotiert ist und das UserDetailsService-Interface implementiert.
        *
        @Bean
        public UserDetailsService userDetailsService() {
            UserDetails user = User.builder()
                    .username("user")
                    .password(passwordEncoder().encode("password"))
                    .roles("USER")
                    .build();
        
            return new InMemoryUserDetailsManager(user);
        }
        */

        @Bean
        public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        // ...
    }


    
