package com.example.demo.labrest.dto;

import jakarta.validation.constraints.*;
import lombok.*;

@Data @NoArgsConstructor @AllArgsConstructor
public class NoticeRequestTo {
    @NotNull private Long topicId;
    @NotBlank @Size(min = 2, max = 2048) private String content;
}