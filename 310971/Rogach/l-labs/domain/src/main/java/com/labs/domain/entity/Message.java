package com.labs.domain.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "tbl_message", schema = "distcomp")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Message {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "tweet_id", nullable = false)
    private Tweet tweet;

    @Column(nullable = false, length = 2048)
    private String content;
}

