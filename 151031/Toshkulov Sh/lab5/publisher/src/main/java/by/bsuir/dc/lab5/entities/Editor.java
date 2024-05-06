package by.bsuir.dc.lab5.entities;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.io.Serializable;

@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name="tbl_editor",schema = "public")
public class Editor implements Serializable {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id",nullable = false)
    private Long id;

    @Column(name = "login",nullable = false)
    private String login;
    @Column(name = "password",nullable = false)
    private String password;

    @Column(name="firstname",nullable = false)
    private String firstname;

    @Column(name= "lastname",nullable = false)
    private String lastname;
}
