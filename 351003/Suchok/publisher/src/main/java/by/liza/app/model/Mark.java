package by.liza.app.model;

import jakarta.persistence.*;
import lombok.*;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(
        name = "tbl_mark",
        schema = "distcomp",
        uniqueConstraints = @UniqueConstraint(name = "uq_mark_name", columnNames = "name")
)
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@EqualsAndHashCode(of = "id")
@ToString(exclude = "articles")
public class Mark {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true, length = 32)
    private String name;

    @ManyToMany(mappedBy = "marks", fetch = FetchType.LAZY)
    @Builder.Default
    private List<Article> articles = new ArrayList<>();
}