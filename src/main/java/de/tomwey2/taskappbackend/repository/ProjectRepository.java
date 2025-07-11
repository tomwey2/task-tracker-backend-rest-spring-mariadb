package de.tomwey2.taskappbackend.repository;

import de.tomwey2.taskappbackend.model.Project;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ProjectRepository extends JpaRepository<Project, Long> {
    // Spring Data JPA generiert die Abfrage automatisch aus dem Methodennamen.
    Optional<Project> findByName(String name);

    @Query("SELECT p FROM Project p WHERE " +
            ":name IS NULL OR LOWER(p.name) LIKE LOWER(CONCAT('%', :name, '%'))")
    List<Project> searchProjects(@Param("name") String name);
}