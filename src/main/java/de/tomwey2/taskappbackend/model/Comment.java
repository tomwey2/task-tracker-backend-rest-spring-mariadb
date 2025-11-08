package de.tomwey2.taskappbackend.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.Data;

@Data // Erzeugt automatisch Getter, Setter, toString(), equals(), hashCode()
@Entity // Sagt JPA, dass dies eine Datenbank-Tabelle ist
@Table(name = "comments")
public class Comment extends Auditable {
    @Lob // Large Object, wird als TEXT-Datentyp in der DB gespeichert
    @Column(nullable = false)
    private String content;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "task_id", nullable = false)
    @JsonIgnore // Verhindert Endlosschleifen bei der JSON-Serialisierung
    private Task task;

    // --- Beziehung zum Autor (User) ---

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "author_id", nullable = false)
    @JsonIgnore // Verhindert ebenfalls Serialisierungsprobleme
    private User author;

}
