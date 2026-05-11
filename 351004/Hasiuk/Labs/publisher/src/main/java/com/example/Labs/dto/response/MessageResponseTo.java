package com.example.Labs.dto.response;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class MessageResponseTo {
    private Long id;
    private Long storyId;
    private String content;
    private String state;
}