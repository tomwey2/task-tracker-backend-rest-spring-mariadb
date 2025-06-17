package de.tomwey2.taskappbackend.repository;

import de.tomwey2.taskappbackend.model.Project;
import de.tomwey2.taskappbackend.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ProjectRepository extends JpaRepository<Project, Long> {
    // Spring Data JPA generiert die Abfrage automatisch aus dem Methodennamen.
    Optional<Project> findByName(String name);
}