package de.tomwey2.taskappbackend.service;

import de.tomwey2.taskappbackend.dto.CommentRequestDto;
import de.tomwey2.taskappbackend.exception.ResourceNotFoundException;
import de.tomwey2.taskappbackend.model.Comment;
import de.tomwey2.taskappbackend.model.Task;
import de.tomwey2.taskappbackend.model.User;
import de.tomwey2.taskappbackend.repository.CommentRepository;
import de.tomwey2.taskappbackend.repository.TaskRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional // Wichtig für Operationen, die Lesen und Schreiben
public class CommentService {

    private final CommentRepository commentRepository;
    private final TaskRepository taskRepository;
    private final AuthService authService;
    private final CommentSecurityService commentSecurityService;

    /**
     * Holt alle Kommentare für einen Task.
     */
    public List<Comment> getCommentsForTask(Long taskId) {
        if (!taskRepository.existsById(taskId)) {
            throw new ResourceNotFoundException("Task not found with id: " + taskId);
        }
        return commentRepository.findByTaskId(taskId);
    }

    /**
     * Holt einen spezifischen Kommentar für einen Task.
     */
    public Comment getComment(Long taskId, Long commentId) {
        return commentRepository.findByIdAndTaskId(commentId, taskId)
                .orElseThrow(() -> new ResourceNotFoundException("Comment not found with id " + commentId + " for task " + taskId));
    }

    /**
     * Erstellt einen neuen Kommentar.
     */
    public Comment createComment(Long taskId, CommentRequestDto dto) {
        User author = authService.getCurrentUser();
        Task task = taskRepository.findById(taskId)
                .orElseThrow(() -> new ResourceNotFoundException("Task not found with id: " + taskId));

        Comment comment = new Comment();
        comment.setContent(dto.content());
        comment.setTask(task);
        comment.setAuthor(author);

        return commentRepository.save(comment);
    }

    /**
     * Aktualisiert einen bestehenden Kommentar.
     */
    public Comment updateComment(Long taskId, Long commentId, CommentRequestDto dto) {
        Comment existingComment = getComment(taskId, commentId);

        // Sicherheitsprüfung: Nur der Autor darf bearbeiten
        commentSecurityService.checkIsAuthor(existingComment);

        existingComment.setContent(dto.content());
        return commentRepository.save(existingComment);
    }

    /**
     * Löscht einen Kommentar.
     */
    public void deleteComment(Long taskId, Long commentId) {
        Comment comment = getComment(taskId, commentId);

        // Sicherheitsprüfung: Nur der Autor darf löschen
        commentSecurityService.checkIsAuthor(comment);

        commentRepository.delete(comment);
    }
}