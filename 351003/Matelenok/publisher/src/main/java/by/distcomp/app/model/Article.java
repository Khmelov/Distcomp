package by.distcomp.app.model;
import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.*;
import java.time.OffsetDateTime;
import java.util.HashSet;
import java.util.Set;

@Entity
@Getter
@Setter
@NoArgsConstructor
@Table(name="tbl_article")
public class Article {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(length = 64, unique = true, nullable = false)
    @Size(min = 2, max = 64)
    private String title;

    @Column(length = 2048, nullable = false)
    @Size(min = 4, max = 2048)
    private String content;

    @PastOrPresent
    private OffsetDateTime created;

    @PastOrPresent
    private OffsetDateTime modified;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;

    @ManyToMany(cascade = {CascadeType.PERSIST, CascadeType.MERGE})
    @JoinTable(
            name = "tbl_article_sticker",
            joinColumns = @JoinColumn(name = "article_id"),
            inverseJoinColumns = @JoinColumn(name = "sticker_id")
    )
    private Set<Sticker> stickers = new HashSet<>();

    public void addSticker(Sticker sticker) {
        stickers.add(sticker);
        sticker.getArticles().add(this);
    }

    public void clearStickers() {
        for (Sticker sticker : new HashSet<>(this.stickers)) {
            sticker.getArticles().remove(this);
        }
        this.stickers.clear();
    }
}