package com.example.discussion.model;

import lombok.Data;
import org.springframework.data.cassandra.core.mapping.Column;
import org.springframework.data.cassandra.core.mapping.PrimaryKey;
import org.springframework.data.cassandra.core.mapping.Table;

@Data
@Table("tbl_note")
public class Note {
    @PrimaryKey
    private Long id;

    @Column("article_id")
    private Long articleId;

    private String content;

    private String state; // PENDING, APPROVE, DECLINE
}