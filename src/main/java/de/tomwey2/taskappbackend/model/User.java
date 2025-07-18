package de.tomwey2.taskappbackend.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import de.tomwey2.taskappbackend.Constants;
import jakarta.persistence.*;
import lombok.Data;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.List;
import java.util.Set;

@Data
@Entity
@Table(name = "users")
public class User extends Auditable implements UserDetails {

    @Column(nullable = false, unique = true)
    private String username;

    @Column(nullable = false, unique = true)
    private String email;

    // In einer echten Anwendung sollte das Passwort immer verschlüsselt (gehasht) werden!
    @Column(nullable = false)
    @JsonIgnore // Das Passwort nie in der API-Antwort mitsenden!
    private String password;

    // Annahme: Wir fügen ein einfaches Feld für die Rolle hinzu.
    // In einer komplexeren App wäre dies eine eigene @ManytoMany Role-Entität.
    private String role = Constants.ROLE_USER;

    // Ein User kann viele Tasks haben.
    // 'mappedBy' zeigt auf das Feld in der Task-Klasse, das diese Beziehung besitzt.
    @OneToMany(mappedBy = "reportedBy")
    @JsonIgnore // WICHTIG: Verhindert die Endlos-Rekursion bei der JSON-Ausgabe
    private Set<Task> reportedTasks;

    @OneToMany(mappedBy = "assignedTo")
    @JsonIgnore
    private Set<Task> assignedTasks;

    // Ab hier kommen die Methoden für das Interface UserDetails
    // getPassword() wird von Lombok @Data bereits generiert.
    // getUsername() wird von Lombok @Data bereits generiert.

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        // Wir wandeln unser einfaches 'role'-Feld in eine GrantedAuthority um.
        return List.of(new SimpleGrantedAuthority(this.role));
    }

    @Override
    public boolean isAccountNonExpired() {
        // Fürs Erste gehen wir davon aus, dass Konten nie ablaufen.
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        // Konten sind nie gesperrt.
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        // Anmeldedaten laufen nie ab.
        return true;
    }

    @Override
    public boolean isEnabled() {
        // Konten sind immer aktiviert.
        return true;
    }
}