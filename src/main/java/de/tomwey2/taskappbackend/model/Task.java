package de.tomwey2.taskappbackend.model;

import de.tomwey2.taskappbackend.Constants;
import jakarta.persistence.*;
import lombok.Data; // Lombok für weniger Code
import java.time.LocalDateTime;

@Data // Erzeugt automatisch Getter, Setter, toString(), equals(), hashCode()
@Entity // Sagt JPA, dass dies eine Datenbank-Tabelle ist
@Table(name = "tasks")
public class Task {

    @Id // Definiert den Primärschlüssel
    @GeneratedValue(strategy = GenerationType.IDENTITY) // Sorgt für Auto-Increment des Schlüssels
    private Long id;

    private String title;
    private String description;
    private String state = Constants.TASK_OPEN;
    private LocalDateTime createdAt = LocalDateTime.now(); // Zeitstempel bei Erstellung
    private LocalDateTime updatedAt = null; // Zeitstempel letztes Update

    @ManyToOne( // Definiert, dass viele Tasks (Many) zu einem User (One) gehören.
            fetch = FetchType.LAZY // Eine Performance-Optimierung: Der zugehörige User wird erst aus der
            // Datenbank geladen, wenn er wirklich gebraucht wird.
    )
    @JoinColumn( // Definiert die Fremdschlüssel-Spalte in der tasks-Tabelle.
            name = "reported_by_user_id", // So wird die Spalte in der Datenbank heißen.
            nullable = false // Stellt sicher, dass jeder Task einem Benutzer zugeordnet sein muss.
    )
    private User reportedBy; // Name des Feldes wie gewünscht
}