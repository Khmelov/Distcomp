package com.example.task330.domain.entity;

import lombok.*;
import org.springframework.data.cassandra.core.cql.PrimaryKeyType;
import org.springframework.data.cassandra.core.mapping.Column;
import org.springframework.data.cassandra.core.mapping.Table;
import org.springframework.data.cassandra.core.mapping.PrimaryKeyColumn;

@Table("tbl_reaction")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Reaction {

    @PrimaryKeyColumn(name = "country", ordinal = 0, type = PrimaryKeyType.PARTITIONED)
    private String country;

    @PrimaryKeyColumn(name = "tweetId", ordinal = 1, type = PrimaryKeyType.CLUSTERED)
    private Long tweetId;

    @PrimaryKeyColumn(name = "id", ordinal = 2, type = PrimaryKeyType.CLUSTERED)
    private Long id;

    @Column("content")
    private String content;
}