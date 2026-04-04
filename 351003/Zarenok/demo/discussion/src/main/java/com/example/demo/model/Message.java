package com.example.demo.model;

import lombok.Getter;
import lombok.Setter;
import org.springframework.data.cassandra.core.mapping.PrimaryKey;
import org.springframework.data.cassandra.core.mapping.Table;

@Getter
@Setter
@Table("tbl_message")
public class Message {
    @PrimaryKey
    private MessageKey key;

    private String content;
    private String state;
    public Message() {}

    public Message(MessageKey key, String content, String state) {
        this.key = key;
        this.content = content;
        this.state = state;
    }
}
