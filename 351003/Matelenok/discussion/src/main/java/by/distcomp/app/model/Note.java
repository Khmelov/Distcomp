package by.distcomp.app.model;

import lombok.Getter;
import lombok.Setter;
import org.springframework.data.cassandra.core.mapping.*;

import java.time.Instant;

@Table("tbl_note")
@Getter @Setter
public class Note {

    @PrimaryKey
    private Long id;

    @Column("article_id")
    @Indexed
    private Long articleId;

    @Column("content")
    private String content;

    @Column("created")
    private Instant created = Instant.now();
}