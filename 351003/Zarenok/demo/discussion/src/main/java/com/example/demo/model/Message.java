package com.example.demo.model;

import lombok.Getter;
import lombok.Setter;
import org.springframework.data.cassandra.core.mapping.Column;
import org.springframework.data.cassandra.core.mapping.PrimaryKey;
import org.springframework.data.cassandra.core.mapping.Table;
import java.util.UUID;

@Getter
@Setter
@Table("tbl_message")
public class Message {
    @PrimaryKey
    private MessageKey key;

    private String content;
    public Message() {}

    public Message(MessageKey key, String content) {
        this.key = key;
        this.content = content;
    }
}
