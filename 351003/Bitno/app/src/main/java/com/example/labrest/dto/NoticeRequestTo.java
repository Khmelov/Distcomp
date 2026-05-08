package com.example.labrest.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import jakarta.validation.constraints.*;

@Data @NoArgsConstructor @AllArgsConstructor
public class NoticeRequestTo {
    @NotNull private Long topicId;
    @NotBlank @Size(min = 2, max = 2048) private String content;
}