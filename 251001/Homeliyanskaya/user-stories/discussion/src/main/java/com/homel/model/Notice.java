package com.homel.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.cassandra.core.mapping.PrimaryKey;
import org.springframework.data.cassandra.core.mapping.Table;
import org.springframework.data.annotation.Id;
import org.springframework.data.cassandra.core.mapping.Column;

import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Table("tbl_notice")  // Имя таблицы в Cassandra
public class Notice {

    @PrimaryKey
    private UUID id;  // Идентификатор Notice, UUID по умолчанию

    @Column("story_id")
    private UUID storyId;  // Идентификатор связанной Story, сохраняется только ID

    @Column("content")
    private String content;
}
