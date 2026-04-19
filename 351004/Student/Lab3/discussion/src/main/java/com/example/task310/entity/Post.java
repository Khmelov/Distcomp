package com.example.task310.entity;

import org.springframework.data.cassandra.core.mapping.Column;
import org.springframework.data.cassandra.core.mapping.PrimaryKey;
import org.springframework.data.cassandra.core.mapping.Table;
import lombok.Data;

@Data
@Table("tbl_post")
public class Post {

    @PrimaryKey
    private Long id;

    @Column("country")
    private String country;

    @Column("issue_id")
    private Long issueId;

    @Column("content")
    private String content;
}