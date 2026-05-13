package by.bsuir.publisher.domain;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

import java.time.LocalDateTime;
import java.util.List;

@Entity
@Table(name = "tbl_news")
@SuperBuilder
@NoArgsConstructor
@Getter
@Setter
public class News extends BaseEntity {
    @ManyToOne
    private Writer writer;

    @OneToMany(mappedBy = "news")
    private List<NewsTag> newsTag;

    @Column(unique = true, nullable = false)
    private String title;
    private String content;
    private LocalDateTime created;
    private LocalDateTime modified;
}
