package de.tomwey2.taskappbackend.config;

import java.util.Arrays;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

// Die moderne Art, Spring Security zu konfigurieren (seit Spring Boot 3), ist über eine Konfigurationsklasse,
// die eine SecurityFilterChain-Bean definiert. Der alte WebSecurityConfigurerAdapter ist veraltet (deprecated)
// und sollte nicht mehr verwendet werden.
@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig {

    private final JwtAuthenticationFilter jwtAuthFilter;

    /*
    Wenn wir die User aus der Datenbank holen ist diese Bean nicht mehr notwendig.
    Zur Dokumentation lasse ich diese Funktion noch auskommentiert im Code.
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
    */

    // Dies ist das Herzstück. Hier legen wir fest, wer auf welche Endpunkte zugreifen darf.
    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
            // 1. CSRF-Schutz deaktivieren. Für zustandslose REST-APIs, die nicht
            //    von Browsern mit Sessions genutzt werden, ist dies üblich.
            .csrf(csrf -> csrf.disable())
            // 2. Aktiviert die CORS-Konfiguration, die unten definiert wird
            .cors(Customizer.withDefaults())
            // 3. Die Autorisierungsregeln für HTTP-Requests definieren.
            .authorizeHttpRequests(authz ->
                authz
                    // Erlaube Zugriff auf alle Auth-Endpunkte für Login uns Register
                    .requestMatchers("/api/auth/**")
                    .permitAll()
                    .requestMatchers("/api/users/**")
                    .permitAll()
                    // Regeln, um die OpenAPI-Dokumentation öffentlich zu machen
                    .requestMatchers(
                        "/v3/api-docs/**",
                        "/swagger-ui.html",
                        "/swagger-ui/**"
                    )
                    .permitAll()
                    // Jede andere Anfrage, auch GET Pfade, an die API erfordert eine Authentifizierung.
                    .anyRequest()
                    .authenticated()
            )
            // 4. das Session-Management als STATELESS konfigurieren
            // Spring Security anweisen, keine HttpSessions mehr zu erstellen. Die App ist jetzt zustandslos.
            .sessionManagement(session ->
                session.sessionCreationPolicy(SessionCreationPolicy.STATELESS)
            )
            // Unseren JwtAuthenticationFilter in die Filterkette einhängen, und zwar vor dem Standard-Filter zur Passwort-Authentifizierung.
            .addFilterBefore(
                jwtAuthFilter,
                UsernamePasswordAuthenticationFilter.class
            );

        // HTTP Basic ist für eine token-basierte API nicht mehr nötig
        // .httpBasic(Customizer.withDefaults());

        return http.build();
    }

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();
        // Hier die URL deines React-Frontends eintragen
        configuration.setAllowedOrigins(Arrays.asList("http://localhost:3000"));
        // Erlaube alle gängigen HTTP-Methoden
        configuration.setAllowedMethods(
            Arrays.asList("GET", "POST", "PUT", "DELETE", "OPTIONS")
        );
        // Erlaube alle Header
        configuration.setAllowedHeaders(Arrays.asList("*"));
        // Erlaube das Senden von Credentials (wichtig für Security-Header und Cookies)
        configuration.setAllowCredentials(true);

        UrlBasedCorsConfigurationSource source =
            new UrlBasedCorsConfigurationSource();
        // Wende diese Konfiguration auf alle Pfade in deiner Anwendung an
        source.registerCorsConfiguration("/**", configuration);

        return source;
    }

    // Wir müssen Spring Securitys zentralen AuthenticationManager als Bean verfügbar machen,
    // damit wir ihn in unserem Controller verwenden können.
    @Bean
    public AuthenticationManager authenticationManager(
        AuthenticationConfiguration authenticationConfiguration
    ) throws Exception {
        return authenticationConfiguration.getAuthenticationManager();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        // BCrypt ist der De-facto-Standard zum Hashen von Passwörtern.
        return new BCryptPasswordEncoder();
    }
}
