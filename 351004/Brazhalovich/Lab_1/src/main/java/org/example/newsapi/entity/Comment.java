package org.example.newsapi.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "tbl_comment")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Comment {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "comment_seq_gen")
    @SequenceGenerator(name = "comment_seq_gen", sequenceName = "comment_seq", allocationSize = 1)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "news_id", nullable = false)
    @ToString.Exclude
    private News news;

    @Column(nullable = false, length = 2048)
    private String content;
}