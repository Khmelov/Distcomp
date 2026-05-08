package com.sergey.orsik.discussion.cassandra;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.cassandra.core.mapping.PrimaryKeyClass;
import org.springframework.data.cassandra.core.mapping.PrimaryKeyColumn;

import java.io.Serializable;
import java.time.Instant;

import static org.springframework.data.cassandra.core.cql.PrimaryKeyType.CLUSTERED;
import static org.springframework.data.cassandra.core.cql.PrimaryKeyType.PARTITIONED;

@PrimaryKeyClass
@Data
@NoArgsConstructor
@AllArgsConstructor
public class CommentByTweetKey implements Serializable {

    @PrimaryKeyColumn(name = "tweet_id", ordinal = 0, type = PARTITIONED)
    private Long tweetId;

    @PrimaryKeyColumn(name = "created", ordinal = 1, type = CLUSTERED)
    private Instant created;

    @PrimaryKeyColumn(name = "id", ordinal = 2, type = CLUSTERED)
    private Long id;
}
