package com.example.publisher.entity;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "tbl_note")
public class Note {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private Long tweetId;
    private String content;
    private String state;

    public Note(String content, Long tweetId) {
        this.content = content;
        this.tweetId = tweetId;
        this.state = "PENDING";
    }
}