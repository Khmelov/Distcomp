package by.tracker.rest_api.dto;

import lombok.Data;

@Data
public class NoteResponseDto {
    private Long id;
    private Long tweetId;
    private String content;
}