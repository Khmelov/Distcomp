package by.distcomp.task1.model;
import jakarta.persistence.*;
import jakarta.validation.constraints.Size;
import lombok.*;

import java.util.HashSet;
import java.util.Set;

@Entity
@Getter
@Setter
@NoArgsConstructor
@Table(name="stickers")
public class Sticker {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(length = 32, unique = true, nullable = false)
    @Size(min = 2, max = 32)
    private String name;
    @ManyToMany(mappedBy = "stickers")
    private Set<Article> articles = new HashSet<>();
    public void addArticle(Article article) {
        articles.add(article);
        article.getStickers().add(this);
    }

    public void removeArticle(Article article) {
        articles.remove(article);
        article.getStickers().remove(this);
    }
}
