package by.egorsosnovski.distcomp.entities;

import jakarta.validation.constraints.Size;
import lombok.*;
import org.springframework.data.cassandra.core.mapping.Indexed;
import org.springframework.data.cassandra.core.mapping.PrimaryKey;
import org.springframework.data.cassandra.core.mapping.Table;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table
public class Note {
    @PrimaryKey
    private NoteKey id;
    @Size(min = 2, max = 32)
    String content;
    @Indexed
    private long tweetId;

}
