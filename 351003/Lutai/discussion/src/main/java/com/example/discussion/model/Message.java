package com.example.discussion.model;

import com.example.common.dto.model.enums.MessageState;
import lombok.*;
import org.springframework.data.cassandra.core.mapping.Column;
import org.springframework.data.cassandra.core.mapping.PrimaryKey;
import org.springframework.data.cassandra.core.mapping.Table;

@Table("tbl_message")
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class Message {

    @PrimaryKey
    private MessageKey key;

    @Column("country")
    private String country;

    @Column("content")
    private String content;

    @Column("state")
    private MessageState state;

}