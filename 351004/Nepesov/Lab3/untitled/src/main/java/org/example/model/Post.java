package org.example.model;

import lombok.*;
import org.springframework.data.cassandra.core.mapping.Column;
import org.springframework.data.cassandra.core.mapping.PrimaryKey;
import org.springframework.data.cassandra.core.mapping.Table;

import java.util.UUID;

@Table("tbl_post")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Post {

    @PrimaryKey
    private Long id;

    @Column("news_id")
    private Long newsId;

    private String content;
}