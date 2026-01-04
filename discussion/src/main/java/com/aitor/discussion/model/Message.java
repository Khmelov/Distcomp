package com.aitor.discussion.model;

import lombok.*;
import org.springframework.data.annotation.Transient;
import org.springframework.data.cassandra.core.mapping.*;

@Data
@ToString
@NoArgsConstructor
@Table(value = "tbl_message", keyspace = "distcomp")
public class Message {
    public enum Status {
        PENDING, APPROVE, DELCINE
    }

    static private long curIndex = 0;

    @PrimaryKey
    Long id;

    @Column
    Long issueId;

    @Column
    String content;

    @Column
    Status status = Status.PENDING;

    public Message(Long issueId, String content){
        id = ++curIndex;
        this.issueId = issueId;
        this.content = content;
    }
}
