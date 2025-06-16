package de.tomwey2.taskappbackend.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;
import org.springframework.security.web.SecurityFilterChain;

// Die moderne Art, Spring Security zu konfigurieren (seit Spring Boot 3), ist über eine Konfigurationsklasse,
// die eine SecurityFilterChain-Bean definiert. Der alte WebSecurityConfigurerAdapter ist veraltet (deprecated)
// und sollte nicht mehr verwendet werden.
@Configuration
@EnableWebSecurity
public class SecurityConfig {
    // Den PasswordEncoder und einen In-Memory-Benutzer definieren
    // Passwörter dürfen niemals im Klartext gespeichert werden, auch nicht für Testbenutzer im Speicher.
    // Wir müssen sie verschlüsseln. Dafür definieren wir einen PasswordEncoder als Bean.
    // Anschließend legen wir einen Testbenutzer an.

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

    // Dies ist das Herzstück. Hier legen wir fest, wer auf welche Endpunkte zugreifen darf.
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