package de.tomwey2.taskappbackend.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;

@Getter
@Setter
@MappedSuperclass // 1. Sagt JPA, dass die Felder dieser Klasse in die Tabellen der erbenden Entitäten aufgenommen werden sollen.
@EntityListeners(AuditingEntityListener.class) // 2. Aktiviert das Auditing für diese Klasse und ihre Erben.
public abstract class Auditable {

    @Id // Definiert den Primärschlüssel
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    protected Long id; // protected, damit es nicht Teil der öffentlichen API ist, aber vererbt wird

    @CreatedDate // 3. Markiert dieses Feld für das Erstellungsdatum.
    @Column(nullable = false, updatable = false) // Das Erstellungsdatum soll nicht nachträglich änderbar sein.
    protected LocalDateTime createdAt;

    @LastModifiedDate // 4. Markiert dieses Feld für das Änderungsdatum.
    protected LocalDateTime updatedAt;
}