package by.kopvzakone.distcomp.entities;

import jakarta.persistence.*;
import jakarta.validation.constraints.Size;
import lombok.*;

@Entity
@Getter
@Setter
@ToString
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "tbl_editor")
public class Editor {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    long id;

    @Column(unique = true)
    @Size(min = 2, max = 64)
    String login;

    @Size(min = 8, max = 128)
    String password;

    @Size(min = 2, max = 64)
    String firstname;

    @Size(min = 2, max = 64)
    String lastname;
}
