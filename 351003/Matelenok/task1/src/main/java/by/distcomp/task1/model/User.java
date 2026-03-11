package by.distcomp.task1.model;
import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.*;

import java.util.List;

@Entity
@Getter
@Setter
@NoArgsConstructor
@Table(name="users")
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(length = 64,unique = true, nullable = false)
    @Size(min = 2, max = 64)
    private String login;
    @Column(length = 128, nullable = false)
    @Size(min = 8, max = 128)
    private String password;
    @Column(length = 64, nullable = false)
    @Size(min = 2, max = 64)
    private String firstname;
    @Column(length = 64, nullable = false)
    @Size(min = 2, max = 64)
    private String lastname;

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL)
    private List<Article> articles;

}
