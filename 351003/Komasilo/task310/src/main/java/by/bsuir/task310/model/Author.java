package by.bsuir.task310.model;

import jakarta.persistence.*;
import lombok.Data;

@Entity
@Table(name = "tbl_author")
@Data
public class Author {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 64, unique = true)
    private String login;

    @Column(nullable = false, length = 128)
    private String password;

    @Column(nullable = false, length = 64)
    private String firstname;

    @Column(nullable = false, length = 64)
    private String lastname;

    @Column(nullable = false)
    private String role = "CUSTOMER";
}