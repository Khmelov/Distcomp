package by.boukhvalova.distcomp.entities;

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
@Table(name = "tbl_tweet")
public class Tweet {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    long id;

    @Column(name = "user_id")
    long userId;

    @Size(min = 2, max = 64)
    String title;

    @Size(min = 4, max = 2048)
    String content;

    Timestamp created;
    Timestamp modified;

    @ManyToMany
    @JoinTable(
            name = "tbl_tweet_sticker",
            joinColumns = @JoinColumn(name = "tweet_id"),
            inverseJoinColumns = @JoinColumn(name = "sticker_id")
    )
    private Set<Sticker> stickers;
}
