package com.aitor.publisher.model;

import jakarta.persistence.*;
import lombok.*;

@Data
@Entity
@NoArgsConstructor
@RequiredArgsConstructor
@Table(name = "tbl_message")
public class Message {
    public enum Status {
        PENDING, APPROVE, DELCINE
    }

    static private long curIndex = 0;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Long id;
    @NonNull
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "issue_id", nullable = false)
    Issue issueId;
    @NonNull
    String content;

    @org.springframework.data.annotation.Transient
    Status status = Status.PENDING;

    public String toString(){
        return String.format("Message(id=%d, issueId=%d, content=%s, status=%s)", id, issueId.getId(), content, status);
    }
}
