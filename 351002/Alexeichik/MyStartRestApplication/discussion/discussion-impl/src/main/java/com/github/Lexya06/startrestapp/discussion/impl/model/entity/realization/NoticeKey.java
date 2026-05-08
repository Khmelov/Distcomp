package com.github.Lexya06.startrestapp.discussion.impl.model.entity.realization;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.springframework.data.cassandra.core.cql.PrimaryKeyType;
import org.springframework.data.cassandra.core.mapping.Indexed;
import org.springframework.data.cassandra.core.mapping.PrimaryKeyClass;
import org.springframework.data.cassandra.core.mapping.PrimaryKeyColumn;

import java.io.Serializable;

@Getter
@Setter
@ToString
@PrimaryKeyClass
public class NoticeKey {
    @PrimaryKeyColumn(
            type = PrimaryKeyType.PARTITIONED,
            ordinal = 0
    )
    private String country;

    @PrimaryKeyColumn(
            name = "article_id",
            type = PrimaryKeyType.PARTITIONED,
            ordinal = 1
    )
    private Long articleId;

    @PrimaryKeyColumn(
            type = PrimaryKeyType.CLUSTERED,
            ordinal = 2
    )
    private Long id;
}
