package org.example.model;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "tbl_post")
@Getter @Setter
@NoArgsConstructor
@AllArgsConstructor
public class Post {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "news_id", nullable = false)
    private News news;

    @Column(nullable = false, length = 2048)
    private String content;
}