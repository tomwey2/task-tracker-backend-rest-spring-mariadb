package de.tomwey2.taskappbackend.repository;

import de.tomwey2.taskappbackend.model.Project;
import de.tomwey2.taskappbackend.model.Task;
import de.tomwey2.taskappbackend.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface TaskRepository extends JpaRepository<Task, Long> {
    // Magie! ✨
    // Spring Data JPA stellt uns automatisch Methoden wie
    // findAll(), findById(), save(), deleteById() etc. zur Verfügung.
    // Wir können hier bei Bedarf auch eigene Abfragemethoden definieren.
    List<Task> findByBelongsTo(Project project);
    List<Task> findByAssignedToAndState(User assignedToUser, String state);
    //List<Task> findByDeadlineBeforeAndStateIsOpen(LocalDate today);

    @Query("SELECT t FROM Task t WHERE t.assignedTo = :user AND t.state = :state")
    List<Task> findTasksByAssigneeAndStatus(@Param("user") User user, @Param("state") String state);

    /**
     * Sucht nach Tasks mit optionalen Filtern für Projekt, zugewiesenen Benutzer und Titel.
     * Die Titelsuche ist eine case-insensitive Substring-Suche.
     *
     * @param projectId ID des Projekts (optional)
     * @param assignedToUserId ID des zugewiesenen Benutzers (optional)
     * @param title Ein Teil des Titels, nach dem gesucht werden soll (optional)
     * @return Eine Liste von passenden Tasks.
     */
    @Query("SELECT t FROM Task t WHERE " +
            "(:projectId IS NULL OR t.belongsTo.id = :projectId) AND " +
            "(:assignedToUserId IS NULL OR t.assignedTo.id = :assignedToUserId) AND " +
            "(:title IS NULL OR LOWER(t.title) LIKE LOWER(CONCAT('%', :title, '%')))")
    List<Task> searchTasks(@Param("projectId") Long projectId,
                           @Param("assignedToUserId") Long assignedToUserId,
                           @Param("title") String title);
}