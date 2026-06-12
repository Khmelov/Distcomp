package com.example.demo.labrest.model;

import lombok.*;
import org.springframework.data.cassandra.core.mapping.Column;
import org.springframework.data.cassandra.core.mapping.PrimaryKey;
import org.springframework.data.cassandra.core.mapping.Table;
import java.io.Serializable;
import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table("tbl_notice")
public class NoticeEntity implements Serializable {

    @PrimaryKey
    private Long id;

    @Column("topic_id")
    private Long topicId;

    @Column
    private String content;

    @Column
    private String country;

    @Column
    private String state;

    @Column
    private LocalDateTime created;

    @Column
    private LocalDateTime modified;
}