package by.shaminko.distcomp.entities;

import jakarta.persistence.*;
import jakarta.validation.constraints.Size;
import lombok.*;

import java.sql.Timestamp;
import java.util.Set;

@Entity
@Getter
@Setter
@ToString
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "tbl_article")
public class Tweet {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    long id;

    @Column(name = "creator_id")
    long creatorId;

    @Size(min = 2, max = 64)
    String title;

    @Size(min = 4, max = 2048)
    String content;

    Timestamp created;
    Timestamp modified;

    @ManyToMany
    @JoinTable(
            name = "tbl_article_marker",
            joinColumns = @JoinColumn(name = "article_id"),
            inverseJoinColumns = @JoinColumn(name = "marker_id")
    )
    private Set<Tag> tags;
}
