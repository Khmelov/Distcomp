package org.discussion.entity.comment;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.cassandra.core.cql.Ordering;
import org.springframework.data.cassandra.core.cql.PrimaryKeyType;
import org.springframework.data.cassandra.core.mapping.PrimaryKeyClass;
import org.springframework.data.cassandra.core.mapping.PrimaryKeyColumn;

import java.io.Serializable;

@Data
@PrimaryKeyClass
@AllArgsConstructor
@NoArgsConstructor
public class CommentKey implements Serializable {

    @PrimaryKeyColumn(name = "country", type = PrimaryKeyType.PARTITIONED)
    private String country;

    @PrimaryKeyColumn(name = "issue_id", type = PrimaryKeyType.CLUSTERED, ordinal = 0)
    private Long issueId;

    @PrimaryKeyColumn(name = "id", type = PrimaryKeyType.CLUSTERED, ordinal = 1, ordering = Ordering.DESCENDING)
    private Long id;
}
