package com.example.demo.model;

import lombok.Getter;
import lombok.Setter;
import org.springframework.data.cassandra.core.cql.PrimaryKeyType;
import org.springframework.data.cassandra.core.mapping.PrimaryKeyClass;
import org.springframework.data.cassandra.core.mapping.PrimaryKeyColumn;

@Getter
@Setter
@PrimaryKeyClass
public class MessageKey {

    @PrimaryKeyColumn(name = "issue_id", ordinal = 0, type = PrimaryKeyType.PARTITIONED)
    private Long issueId;

    @PrimaryKeyColumn(name = "id", ordinal = 1, type = PrimaryKeyType.CLUSTERED)
    private Long id;

    public MessageKey() {}

    public MessageKey(Long issueId, Long id) {
        this.issueId = issueId;
        this.id = id;
    }
}
