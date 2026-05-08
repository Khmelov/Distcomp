package com.example.task310.dto;
import com.example.task310.enums.PostState;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class PostRequestTo {
    private Long id;
    private Long issueId;
    @Size(min = 2, max = 2048)
    private String content;
    private PostState state;
}