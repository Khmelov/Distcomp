package by.liza.app.model;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(
        name = "tbl_article",
        schema = "distcomp",
        uniqueConstraints = @UniqueConstraint(name = "uq_article_title", columnNames = "title")
)
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@EqualsAndHashCode(of = "id")
@ToString(exclude = {"writer", "marks"})
public class Article {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(
            name = "writer_id",
            nullable = false,
            foreignKey = @ForeignKey(name = "fk_article_writer")
    )
    private Writer writer;

    @Column(nullable = false, unique = true, length = 64)
    private String title;

    @Column(nullable = false, columnDefinition = "TEXT")
    private String content;

    @Column(nullable = false)
    private LocalDateTime created;

    @Column(nullable = false)
    private LocalDateTime modified;

    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(
            name = "tbl_article_mark",
            schema = "distcomp",
            joinColumns = @JoinColumn(
                    name = "article_id",
                    foreignKey = @ForeignKey(name = "fk_article_mark_article")
            ),
            inverseJoinColumns = @JoinColumn(
                    name = "mark_id",
                    foreignKey = @ForeignKey(name = "fk_article_mark_mark")
            )
    )
    @Builder.Default
    private List<Mark> marks = new ArrayList<>();
}