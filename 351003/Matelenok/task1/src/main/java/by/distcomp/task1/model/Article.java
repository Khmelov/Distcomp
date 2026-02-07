package by.distcomp.task1.model;
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
@Table(name="article")
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
    @JoinColumn(name = "userId")
    private User user;
    @OneToMany(cascade = CascadeType.ALL)
    private Set<Note> notes = new HashSet<>();
    @ManyToMany
    @JoinTable(
            name = "m2m_article_sticker",
            joinColumns = @JoinColumn(name = "articleId"),
            inverseJoinColumns = @JoinColumn(name = "stickerId")
    )
    private Set<Sticker> stickers = new HashSet<>();
    public void setCreated(OffsetDateTime dateTime) { this.created = dateTime; }
    public void setModified(OffsetDateTime dateTime) {
        this.modified = dateTime;
    }
    public void setTitle(String title) {
        this.title = title;
    }
    public void setId(Long id){this.id = id;}
    public void setContent(String content) {
        this.content = content;
    }
    public void setUser(User user) {
        this.user = user;
    }
    public void addSticker(Sticker sticker) {
        stickers.add(sticker);
        sticker.getArticles().add(this);
    }

    public void removeSticker(Sticker sticker) {
        stickers.remove(sticker);
        sticker.getArticles().remove(this);
    }
    public void addNote(Note note) {
        notes.add(note);
        note.setArticle(this);
    }

    public void removeNote(Note note) {
        notes.remove(note);
        note.setArticle(null);
    }
}