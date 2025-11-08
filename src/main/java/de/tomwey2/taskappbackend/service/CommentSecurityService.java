package de.tomwey2.taskappbackend.service;

import de.tomwey2.taskappbackend.model.Comment;
import de.tomwey2.taskappbackend.model.User;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class CommentSecurityService {

    private final AuthService authService;

    /**
     * Prüft, ob der aktuell eingeloggte Benutzer der Autor des Kommentars ist.
     * Wirft eine AccessDeniedException, falls nicht.
     *
     * @param comment Der zu prüfende Kommentar.
     */
    public void checkIsAuthor(Comment comment) {
        User currentUser = authService.getCurrentUser();
        if (!comment.getAuthor().getId().equals(currentUser.getId())) {
            throw new AccessDeniedException("User is not the author of this comment.");
        }
    }
}