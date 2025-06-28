package de.tomwey2.taskappbackend.controller;

import de.tomwey2.taskappbackend.dto.LoginRequest;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
@Tag(name = "Authentication", description = "Operationen für Login/Authentifizierung")
public class AuthController {

    private final AuthenticationManager authenticationManager;

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody LoginRequest loginRequest) {
        try {
            // Wir erstellen ein Authentifizierungsobjekt mit den vom User gesendeten Daten
            var token = new UsernamePasswordAuthenticationToken(
                    loginRequest.username(), loginRequest.password());

            // Spring Security versucht nun, den User zu authentifizieren
            // (ruft intern unseren JpaUserDetailsService und prüft das Passwort mit dem PasswordEncoder)
            authenticationManager.authenticate(token);

            // Wenn die Authentifizierung erfolgreich war, geben wir 200 OK zurück.
            // In einer echten Anwendung würden wir hier einen JWT-Token generieren und zurückgeben.
            return ResponseEntity.ok().body("Login successful");

        } catch (BadCredentialsException e) {
            // Wenn die Authentifizierung fehlschlägt, geben wir 401 Unauthorized zurück.
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Invalid credentials");
        }
    }
}