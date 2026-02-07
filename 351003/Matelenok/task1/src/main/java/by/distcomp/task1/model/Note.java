package by.distcomp.task1.model;
import jakarta.persistence.*;
import jakarta.validation.constraints.Size;
import lombok.*;
@Entity
@Getter
@Setter
@NoArgsConstructor
@Table(name="notes")
public class Note {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(length = 2048, nullable = false)
    @Size(min = 4, max = 2048)
    private String content;
    @ManyToOne
    @JoinColumn(name="article")
    private Article article;
}
