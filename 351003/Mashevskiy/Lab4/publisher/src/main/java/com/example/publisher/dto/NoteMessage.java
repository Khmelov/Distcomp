package com.example.publisher.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.io.Serializable;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class NoteMessage implements Serializable {
    private Long noteId;
    private Long tweetId;
    private String text;
    private String state;
}