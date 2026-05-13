package org.example.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.cassandra.core.mapping.Column;
import org.springframework.data.cassandra.core.mapping.PrimaryKey;
import org.springframework.data.cassandra.core.mapping.Table;
import java.io.Serializable;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Table("tbl_post")
public class Post implements Serializable {

    @PrimaryKey
    private Long id;

    @Column("news_id")
    private Long newsId;

    @Column("content")
    private String content;

    @Column("state")
    private PostState state;
}