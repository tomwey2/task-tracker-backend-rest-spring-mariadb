package de.tomwey2.taskappbackend.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.Data;

import java.util.Set;

@Data
@Entity
@Table(name = "users")
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String username;

    @Column(nullable = false, unique = true)
    private String email;

    // In einer echten Anwendung sollte das Passwort immer verschlüsselt (gehasht) werden!
    @Column(nullable = false)
    private String password;

    // Ein User kann viele Tasks haben.
    // 'mappedBy' zeigt auf das Feld in der Task-Klasse, das diese Beziehung besitzt.
    @OneToMany(mappedBy = "reportedBy")
    @JsonIgnore // WICHTIG: Verhindert die Endlos-Rekursion bei der JSON-Ausgabe
    private Set<Task> tasks;
}