package de.tomwey2.taskappbackend.model;

import de.tomwey2.taskappbackend.Constants;
import jakarta.persistence.*;
import lombok.Data; // Lombok für weniger Code

import java.time.LocalDate;

@Data // Erzeugt automatisch Getter, Setter, toString(), equals(), hashCode()
@Entity // Sagt JPA, dass dies eine Datenbank-Tabelle ist
@Table(name = "tasks")
public class Task extends Auditable {

    private String title;
    private String description;
    private String state = Constants.TASK_OPEN;
    private LocalDate dueDate;

    @ManyToOne( // Definiert, dass viele Tasks (Many) zu einem User (One) gehören.
            fetch = FetchType.LAZY // Eine Performance-Optimierung: Der zugehörige User wird erst aus der
            // Datenbank geladen, wenn er wirklich gebraucht wird.
    )
    @JoinColumn( // Definiert die Fremdschlüssel-Spalte in der tasks-Tabelle.
            name = "reported_by_user_id", // So wird die Spalte in der Datenbank heißen.
            nullable = false // Stellt sicher, dass jeder Task einem Benutzer zugeordnet sein muss.
    )
    private User reportedBy;

    @ManyToOne(fetch = FetchType.LAZY )
    @JoinColumn(
            name = "belongs_to_project_id",
            nullable = false
    )
    private Project belongsTo;
}