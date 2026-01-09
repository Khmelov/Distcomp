package org.example.task330.discussion.model;

import jakarta.validation.constraints.Size;
import lombok.*;
import org.springframework.data.cassandra.core.cql.Ordering;
import org.springframework.data.cassandra.core.cql.PrimaryKeyType;
import org.springframework.data.cassandra.core.mapping.*;

@Table(value = "tbl_message")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode
public class Message {

    @PrimaryKey
    private MessageKey key;

    @Column("content")
    @Size(min = 2, max = 2048)
    private String content;

    @PrimaryKeyClass
    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    @EqualsAndHashCode
    public static class MessageKey {
        @PrimaryKeyColumn(name = "country", type = PrimaryKeyType.PARTITIONED, ordinal = 0)
        private String country;

        @PrimaryKeyColumn(name = "tweet_id", type = PrimaryKeyType.CLUSTERED, ordering = Ordering.ASCENDING, ordinal = 1)
        private Long tweetId;

        @PrimaryKeyColumn(name = "id", type = PrimaryKeyType.CLUSTERED, ordering = Ordering.ASCENDING, ordinal = 2)
        private Long id;
    }
}

