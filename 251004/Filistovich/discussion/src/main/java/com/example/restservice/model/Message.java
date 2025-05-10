package com.example.restservice.model;

import lombok.Data;
import org.springframework.data.cassandra.core.cql.PrimaryKeyType;
import org.springframework.data.cassandra.core.mapping.Column;
import org.springframework.data.cassandra.core.mapping.PrimaryKeyColumn;
import org.springframework.data.cassandra.core.mapping.Table;

@Data
@Table("tbl_message")
public class Message {

    @PrimaryKeyColumn(name = "country", type = PrimaryKeyType.PARTITIONED)
    private String country;

    @PrimaryKeyColumn(name = "id", type = PrimaryKeyType.CLUSTERED)
    private long id;

    @PrimaryKeyColumn(name = "news_id", type = PrimaryKeyType.CLUSTERED)
    private long newsId;

    @Column("content")
    private String content;

    @Column("status")
    private Status status;

}
