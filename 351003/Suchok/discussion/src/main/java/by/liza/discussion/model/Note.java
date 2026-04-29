package by.liza.discussion.model;

import lombok.*;
import org.springframework.data.cassandra.core.cql.PrimaryKeyType;
import org.springframework.data.cassandra.core.mapping.*;

@Table("tbl_note")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Note {

    @PrimaryKeyColumn(name = "id", ordinal = 0, type = PrimaryKeyType.PARTITIONED)
    private Long id;

    @Column("article_id")
    private Long articleId;

    @Column("content")
    private String content;

    @Column("state")
    private String state;
}