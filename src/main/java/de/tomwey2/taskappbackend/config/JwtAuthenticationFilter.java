package de.tomwey2.taskappbackend.config;

import de.tomwey2.taskappbackend.service.JwtService;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.lang.NonNull;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

/**
 * Dieser Filter wird bei jeder Anfrage ausgeführt, prüft auf einen gültigen JWT und authentifiziert den
 * Benutzer für die Dauer dieser einen Anfrage.
 * <p>
 * Der Workflow sieht so aus:
 * <ul>
 * <li>1. Der Benutzer meldet sich mit Name/Passwort an.</li>
 * <li>2. Das Backend prüft die Daten. Bei Erfolg erzeugt es einen JWT, signiert ihn mit seinem geheimen Schlüssel und
 * sendet ihn an das Frontend.</li>
 * <li>3. Das Frontend speichert diesen JWT (z.B. im localStorage).</li>
 * <li>4. Für jede weitere Anfrage an geschützte Endpunkte hängt das Frontend den JWT im Authorization-Header
 * an (z.B. Authorization: Bearer <token>).</li>
 * <li>5. Das Backend empfängt die Anfrage, prüft die Signatur des Tokens mit seinem geheimen Schlüssel.
 * Wenn die Signatur gültig ist, vertraut das Backend den Informationen im Token und weiß, welcher Benutzer
 * die Anfrage stellt, ohne in einer Session nachschauen zu müssen. Das ist der Kern von "stateless".</li>
 * </ul>
 */
@Component
@RequiredArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JwtService jwtService;
    private final UserDetailsService userDetailsService;

    @Override
    protected void doFilterInternal(
            @NonNull HttpServletRequest request,
            @NonNull HttpServletResponse response,
            @NonNull FilterChain filterChain
    ) throws ServletException, IOException {

        final String authHeader = request.getHeader("Authorization");
        final String jwt;
        final String username;

        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            filterChain.doFilter(request, response);
            return;
        }

        jwt = authHeader.substring(7);
        username = jwtService.extractUsername(jwt);

        if (username != null && SecurityContextHolder.getContext().getAuthentication() == null) {
            UserDetails userDetails = this.userDetailsService.loadUserByUsername(username);
            if (jwtService.isTokenValid(jwt, userDetails)) {
                UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(
                        userDetails,
                        null,
                        userDetails.getAuthorities()
                );
                authToken.setDetails(
                        new WebAuthenticationDetailsSource().buildDetails(request)
                );
                SecurityContextHolder.getContext().setAuthentication(authToken);
            }
        }
        filterChain.doFilter(request, response);
    }
}