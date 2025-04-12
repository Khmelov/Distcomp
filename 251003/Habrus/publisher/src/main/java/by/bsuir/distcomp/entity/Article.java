package by.bsuir.distcomp.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "tbl_article", schema = "public")
public class Article implements Identifiable {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @ManyToOne
    @JoinColumn(name="author_id")
    private Author author;

    @Column(length = 64, nullable = false, unique = true)
    private String title;

    @Column(length = 2048, nullable = false)
    private String content;

    private LocalDateTime created;
    private LocalDateTime modified;

    @OneToMany(mappedBy = "article")
    private List<Reaction> reactions = new ArrayList<>();

    @ManyToMany
    @JoinTable(name="tbl_article_marker",
            joinColumns=@JoinColumn(name="article_id"),
            inverseJoinColumns=@JoinColumn(name="marker_id"))
    private Set<Marker> markers = new HashSet<>();

}
