package com.group310971.gormash.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class MessageRequestTo {

    private Long id;

    @NotNull(message = "Topic ID is required")
    private Long topicId;

    @Size(min = 2, max = 2048, message = "Content must be between 2 and 2048 characters")
    private String content;
}
