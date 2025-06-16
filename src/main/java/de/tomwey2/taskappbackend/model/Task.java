package de.tomwey2.taskappbackend.model;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.Data; // Lombok für weniger Code
import java.time.LocalDateTime;

@Data // Erzeugt automatisch Getter, Setter, toString(), equals(), hashCode()
@Entity // Sagt JPA, dass dies eine Datenbank-Tabelle ist
public class Task {

    @Id // Definiert den Primärschlüssel
    @GeneratedValue(strategy = GenerationType.IDENTITY) // Sorgt für Auto-Increment des Schlüssels
    private Long id;

    private String title;
    private String description;
    private boolean completed = false; // Standardwert false
    private LocalDateTime createdAt = LocalDateTime.now(); // Zeitstempel bei Erstellung

}