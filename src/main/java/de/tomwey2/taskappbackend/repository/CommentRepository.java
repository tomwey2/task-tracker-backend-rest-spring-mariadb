package de.tomwey2.taskappbackend.repository;

import de.tomwey2.taskappbackend.model.Comment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface CommentRepository extends JpaRepository<Comment, Long> {

    /**
     * Findet alle Kommentare, die zu einer bestimmten Task-ID gehören.
     */
    List<Comment> findByTaskId(Long taskId);

    /**
     * Findet einen spezifischen Kommentar anhand seiner ID und der Task-ID.
     * Stellt sicher, dass der Kommentar auch zum angefragten Task gehört.
     */
    Optional<Comment> findByIdAndTaskId(Long id, Long taskId);
}