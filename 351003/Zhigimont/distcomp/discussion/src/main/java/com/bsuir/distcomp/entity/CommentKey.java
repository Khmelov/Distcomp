package com.bsuir.distcomp.entity;

import lombok.Data;
import org.springframework.data.cassandra.core.cql.PrimaryKeyType;
import org.springframework.data.cassandra.core.mapping.PrimaryKeyClass;
import org.springframework.data.cassandra.core.mapping.PrimaryKeyColumn;

import java.io.Serializable;

@Data
@PrimaryKeyClass
public class CommentKey implements Serializable {
    @PrimaryKeyColumn(type = PrimaryKeyType.PARTITIONED)
    private Long topicId;

    @PrimaryKeyColumn(type = PrimaryKeyType.CLUSTERED)
    private Long id;
}