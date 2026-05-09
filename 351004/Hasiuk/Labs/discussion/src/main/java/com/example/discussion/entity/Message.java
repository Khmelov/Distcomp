package com.example.discussion.entity;
import lombok.*;
import org.springframework.data.cassandra.core.mapping.Column;
import org.springframework.data.cassandra.core.mapping.PrimaryKey;
import org.springframework.data.cassandra.core.mapping.Table;

@Table("tbl_message")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor
public class Message {
    @PrimaryKey
    private MessageKey key;
    @Column("content")
    private String content;
    @Column("state")
    private MessageState state;
}