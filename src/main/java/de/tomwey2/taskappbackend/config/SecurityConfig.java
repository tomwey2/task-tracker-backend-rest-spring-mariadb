package de.tomwey2.taskappbackend.config;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
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

                // 2. Die Autorisierungsregeln für HTTP-Requests definieren.
                .authorizeHttpRequests(authz -> authz
                        // Regeln, um die OpenAPI-Dokumentation öffentlich zu machen
                        .requestMatchers("/v3/api-docs/**", "/swagger-ui.html", "/swagger-ui/**").permitAll()
                        // Jede GET-Anfrage, die auf /api/** passt, ist für jeden erlaubt.
                        .requestMatchers(HttpMethod.GET, "/api/**").permitAll()
                        // Jede andere Anfrage an die API erfordert eine Authentifizierung.
                        .anyRequest().authenticated()
                )

                // 3. HTTP Basic Authentication aktivieren.
                .httpBasic(Customizer.withDefaults());

        return http.build();
    }

    /**
     * Globale Informationen und Security konfigurieren
     * Globale Informationen, wie den Titel der API, die Version und die Security-Konfigurationen.
     * Auf der Swagger UI oben rechts befindet sich jetzt ein "Authorize"-Button.
     * Dort kann man user/password-Credentials eingeben, und Swagger UI wird sie automatisch
     * für die Test-Anfragen an die geschützten Endpunkte verwenden.
     *
     * @return
     */
    @Bean
    public OpenAPI customOpenAPI() {
        final String securitySchemeName = "basicAuth";
        return new OpenAPI()
                .info(new Info().title("Task Tracker Backend API")
                        .version("1.0")
                        .description("API für das Task Tracker Backend.")
                        .license(new License().name("Apache 2.0").url("https://github.com/tomwey2/task-tracker-backend-rest-spring-mariadb")))
                // Füge die Security-Definition für Basic Auth hinzu
                .addSecurityItem(new SecurityRequirement().addList(securitySchemeName))
                .components(new Components()
                        .addSecuritySchemes(securitySchemeName, new SecurityScheme()
                                .name(securitySchemeName)
                                .type(SecurityScheme.Type.HTTP)
                                .scheme("basic")
                                .description("HTTP Basic Authentication")));
    }
}