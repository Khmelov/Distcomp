package org.example.task310rest.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.Size;
import lombok.*;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "tbl_writer", schema = "distcomp")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = false)
public class Writer extends BaseEntity {

    @Column(nullable = false, length = 64, unique = true)
    @Size(min = 2, max = 64)
    private String login;

    @Column(nullable = false, length = 128)
    @Size(min = 8, max = 128)
    private String password;

    @Column(nullable = false, length = 64)
    @Size(min = 2, max = 64)
    private String firstname;

    @Column(nullable = false, length = 64)
    @Size(min = 2, max = 64)
    private String lastname;

    @OneToMany(mappedBy = "writer", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Tweet> tweets = new ArrayList<>();
}


