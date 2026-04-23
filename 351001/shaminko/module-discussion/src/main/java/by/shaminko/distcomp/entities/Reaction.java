package by.shaminko.distcomp.entities;

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
@Table("tbl_message")
public class Reaction {

    @PrimaryKey
    private Long id;

    @Size(min = 2, max = 2048)
    String content;

    @Column("article_id")
    private long articleId;
    private String state;
}
