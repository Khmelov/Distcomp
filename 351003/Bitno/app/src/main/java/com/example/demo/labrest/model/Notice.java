package com.example.demo.labrest.model;

import jakarta.persistence.*;
import lombok.*;

@Entity @Table(name = "tbl_notice", schema = "distcomp")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor
public class Notice {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "topic_id", nullable = false)
    private Topic topic;

    @Column(nullable = false, length = 2048) private String content;
    public Long getTopicId() {
        return topic.getId();
    }
}