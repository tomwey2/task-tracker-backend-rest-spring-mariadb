package de.tomwey2.taskappbackend.repository;

import de.tomwey2.taskappbackend.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    // Spring Data JPA generiert die Abfrage automatisch aus dem Methodennamen.
    Optional<User> findByUsername(String username);
}