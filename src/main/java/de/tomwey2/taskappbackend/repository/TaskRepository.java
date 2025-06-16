package de.tomwey2.taskappbackend.repository;

import de.tomwey2.taskappbackend.model.Task;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface TaskRepository extends JpaRepository<Task, Long> {
    // Magie! ✨
    // Spring Data JPA stellt uns automatisch Methoden wie
    // findAll(), findById(), save(), deleteById() etc. zur Verfügung.
    // Wir können hier bei Bedarf auch eigene Abfragemethoden definieren.
}