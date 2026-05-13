package org.example.model;

import lombok.*;
import org.springframework.data.cassandra.core.mapping.PrimaryKey;
import org.springframework.data.cassandra.core.mapping.Table;

@Table("tbl_post")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Post {
    @PrimaryKey
    private Long id;
    private Long newsId;
    private String content;
    private PostState state; // Измени тип с String на PostState
}