package com.example.task310.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "tbl_post")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Post {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "issue_id", nullable = false)
    private Long issueId;

    @Column(name = "content", nullable = false, length = 2048)
    private String content;
}