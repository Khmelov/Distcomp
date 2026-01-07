package com.group310971.gormash.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class TopicRequestTo {

    private Long id;

    @NotNull(message = "Editor ID is required")
    private Long editorId;

    @Size(min = 2, max = 64, message = "Title must be between 2 and 64 characters")
    private String title;

    @Size(min = 4, max = 2048, message = "Content must be between 4 and 2048 characters")
    private String content;

    private List<String> marks;
}
