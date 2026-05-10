package com.adashkevich.nosql.lab.discussion.model;

import org.springframework.data.cassandra.core.mapping.Column;
import org.springframework.data.cassandra.core.mapping.PrimaryKey;
import org.springframework.data.cassandra.core.mapping.Table;

@Table("tbl_message")
public class Message {
    @PrimaryKey
    private MessageKey key;

    @Column("content")
    private String content;

    public MessageKey getKey() { return key; }
    public void setKey(MessageKey key) { this.key = key; }
    public String getContent() { return content; }
    public void setContent(String content) { this.content = content; }
}
