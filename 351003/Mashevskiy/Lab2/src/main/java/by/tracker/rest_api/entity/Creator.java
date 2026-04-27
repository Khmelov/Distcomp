package by.tracker.rest_api.entity;

import jakarta.persistence.*;
import lombok.Data;
import lombok.EqualsAndHashCode;
import java.time.LocalDateTime;

@Data
@Entity
@Table(name = "tbl_creator")
public class Creator {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String login;
    private String password;
    private String firstname;
    private String lastname;
    private LocalDateTime created;
    private LocalDateTime modified;

    @PrePersist
    protected void onCreate() {
        created = LocalDateTime.now();
        modified = LocalDateTime.now();
    }

    @PreUpdate
    protected void onUpdate() {
        modified = LocalDateTime.now();
    }
}