package by.bsuir.discussion.domain;

import by.bsuir.discussion.dto.CommentState;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;
import org.springframework.data.cassandra.core.cql.Ordering;
import org.springframework.data.cassandra.core.cql.PrimaryKeyType;
import org.springframework.data.cassandra.core.mapping.Column;
import org.springframework.data.cassandra.core.mapping.PrimaryKeyColumn;
import org.springframework.data.cassandra.core.mapping.Table;

@Table("tbl_comment")
@SuperBuilder
@NoArgsConstructor
@Getter
@Setter
public class Comment {
    @PrimaryKeyColumn(
            name = "id",
            ordinal = 1,
            type = PrimaryKeyType.CLUSTERED,
            ordering = Ordering.ASCENDING
    )
    private Long id;

    @PrimaryKeyColumn(
            name = "news_id",
            ordinal = 0,
            type = PrimaryKeyType.PARTITIONED
    )
    private Long newsId;

    @Column("content")
    private String content;

    @Column("state")
    private CommentState state;
}
