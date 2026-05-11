package com.example.discussion.model;

import lombok.Data;
import org.springframework.data.cassandra.core.mapping.Column;
import org.springframework.data.cassandra.core.mapping.PrimaryKey;
import org.springframework.data.cassandra.core.mapping.Table;

import java.time.Instant;

@Data
@Table("tbl_post_by_id")
public class PostById {
    @PrimaryKey
    private Long id;

    @Column("story_id")
    private Long storyId;

    private String content;

    @Column("created_at")
    private Instant createdAt;

    @Column("modified_at")
    private Instant modifiedAt;
}
//перенос хранения сущности Post из реляционной базе данных
//Postgres в новый модуль/микросервис с другой базой данных, а именно Cassandra
//Выделен отдельный модуль (по сути микросервис) на порту 24130

