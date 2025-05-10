package com.example.discussion.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.cassandra.core.mapping.*;
import org.springframework.data.cassandra.core.cql.PrimaryKeyType;

import java.io.Serializable;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Table("messages")
public class Message {
    @PrimaryKeyColumn(name = "country", type = PrimaryKeyType.PARTITIONED)
    private String country;

    @PrimaryKeyColumn(name = "id", type = PrimaryKeyType.CLUSTERED)
    private long id;

    @Column("content")
    private String content;

    @Column("issue_id")
    private long issueId;

}