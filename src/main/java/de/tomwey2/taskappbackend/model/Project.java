package de.tomwey2.taskappbackend.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import lombok.Data;

import java.util.Set;

@Data
@Entity
@Table(name = "projects")
public class Project extends Auditable {

    @Column(nullable = false, unique = true)
    private String name;

    @OneToMany(mappedBy = "belongsTo")
    @JsonIgnore
    private Set<Task> tasks;
}
