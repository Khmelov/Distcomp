package by.boukhvalova.distcomp.entities;

import jakarta.validation.constraints.Size;
import lombok.*;
import org.springframework.data.cassandra.core.mapping.Column;
import org.springframework.data.cassandra.core.mapping.PrimaryKey;
import org.springframework.data.cassandra.core.mapping.Table;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table("tbl_note")
public class Note {

    @PrimaryKey
    private Long id;

    @Size(min = 2, max = 2048)
    String content;

    @Column("tweet_id")
    private long tweetId;

    @Column("user_id")
    private long userId;

    private String country;
}
