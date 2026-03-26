package by.distcomp.app.model;
import jakarta.persistence.*;
import jakarta.validation.constraints.Size;
import lombok.*;
@Entity
@Getter
@Setter
@NoArgsConstructor
@Table(name="tbl_note")
public class Note {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(length = 2048, nullable = false)
    @Size(min = 4, max = 2048)
    private String content;
    @ManyToOne
    @JoinColumn(name="article_id")
    private Article article;
}
