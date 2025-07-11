package de.tomwey2.taskappbackend.controller;

import de.tomwey2.taskappbackend.dto.LoginRequest;
import de.tomwey2.taskappbackend.dto.LoginResponse;
import de.tomwey2.taskappbackend.dto.UserModelAssembler;
import de.tomwey2.taskappbackend.dto.UserResponseDto;
import de.tomwey2.taskappbackend.model.User;
import de.tomwey2.taskappbackend.service.JwtService;
import de.tomwey2.taskappbackend.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.hateoas.EntityModel;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
@Tag(name = "Authentication", description = "Operationen that affect the user authentication")
public class AuthController {

    private final AuthenticationManager authenticationManager;
    private final JwtService jwtService;
    private final UserService userService; // UserService injecten
    private final UserModelAssembler userModelAssembler; // Den neuen Assembler injecten

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody LoginRequest loginRequest) {
        try {
            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(loginRequest.username(), loginRequest.password())
            );

            // Wenn die Authentifizierung erfolgreich war, generiere einen Token
            String jwt = jwtService.generateToken((UserDetails) authentication.getPrincipal());

            // Gib den Token in der Antwort zurück
            return ResponseEntity.ok(new LoginResponse(jwt));

        } catch (BadCredentialsException e) {
            // Wenn die Authentifizierung fehlschlägt, geben wir 401 Unauthorized zurück.
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Invalid credentials");
        }
    }

    @GetMapping("/me")
    @Operation(summary = "Returns the current logged in user")
    public EntityModel<UserResponseDto> getCurrentUser(Authentication authentication) {
        // Der 'Authentication'-Parameter wird von Spring Security automatisch bereitgestellt
        String username = authentication.getName();
        User currentUser = userService.getUserByUsername(username);
        return userModelAssembler.toModel(currentUser);
    }
}