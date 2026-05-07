package com.messageservice.models;

import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.cassandra.core.mapping.Column;
import org.springframework.data.cassandra.core.mapping.PrimaryKey;
import org.springframework.data.cassandra.core.mapping.Table;

@Table("tbl_message_by_tweet")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MessageByTweet {

    @PrimaryKey
    private MessageByTweetKey key;

    @Column("content")
    @Size(min = 2, max = 2048)
    private String content;
}
