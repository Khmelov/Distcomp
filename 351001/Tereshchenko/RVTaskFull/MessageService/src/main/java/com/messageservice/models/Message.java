package com.messageservice.models;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.cassandra.core.cql.PrimaryKeyType;
import org.springframework.data.cassandra.core.mapping.Column;
import org.springframework.data.cassandra.core.mapping.PrimaryKeyColumn;
import org.springframework.data.cassandra.core.mapping.Table;

@Table("tbl_message")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Message {

    @PrimaryKeyColumn(name = "id", type = PrimaryKeyType.PARTITIONED)
    private Long id;

    @NotNull
    @Column("tweet_id")
    private Long tweetId;

    @NotNull
    @Column("bucket")
    private Integer bucket;

    @Column("content")
    @Size(min = 2, max = 2048)
    private String content;

    @NotNull
    @Column("state")
    private MessageState state;
}
