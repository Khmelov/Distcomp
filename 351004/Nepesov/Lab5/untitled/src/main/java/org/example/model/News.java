package org.example.model;

import lombok.*;
import org.springframework.data.cassandra.core.mapping.Column;
import org.springframework.data.cassandra.core.mapping.PrimaryKey;
import org.springframework.data.cassandra.core.mapping.Table;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Table("tbl_news")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor
public class News {
    @PrimaryKey
    private Long id; // Возвращаем Long
    private Long editorId;
    private String title;
    private String content;
    private LocalDateTime created;
    private LocalDateTime modified;
    private List<String> stickers;
}