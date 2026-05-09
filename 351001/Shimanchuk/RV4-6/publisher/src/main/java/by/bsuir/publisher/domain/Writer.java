package by.bsuir.publisher.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

import java.util.List;

@Entity
@Table(name = "tbl_writer")
@SuperBuilder
@NoArgsConstructor
@Getter
@Setter
public class Writer extends BaseEntity {
    @Column(unique = true)
    private String login;
    private String password;
    private String firstname;
    private String lastname;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 32)
    private Role role;

    @OneToMany(mappedBy = "writer")
    private List<News> stories;
}
