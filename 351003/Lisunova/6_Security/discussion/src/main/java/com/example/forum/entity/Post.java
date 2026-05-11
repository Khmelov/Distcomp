package com.example.forum.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.cassandra.core.mapping.CassandraType;
import org.springframework.data.cassandra.core.mapping.Column;
import org.springframework.data.cassandra.core.mapping.PrimaryKeyColumn;
import org.springframework.data.cassandra.core.mapping.Table;
import org.springframework.data.cassandra.core.cql.PrimaryKeyType;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Table("tbl_post")
public class Post {
    @PrimaryKeyColumn(name = "topic_id", ordinal = 0, type = PrimaryKeyType.PARTITIONED)
    private Long topicId;

    @PrimaryKeyColumn(name = "id", ordinal = 1, type = PrimaryKeyType.CLUSTERED)
    private Long id;

    @Column("content")
    private String content;

    @Column("state")
    @CassandraType(type = CassandraType.Name.TEXT)
    private PostState state;
}