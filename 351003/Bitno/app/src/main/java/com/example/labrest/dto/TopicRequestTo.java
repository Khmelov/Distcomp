package com.example.labrest.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import jakarta.validation.constraints.*;

import java.util.HashSet;
import java.util.Set;

@Data @NoArgsConstructor @AllArgsConstructor
public class TopicRequestTo {
    @NotNull private Long creatorId;
    @NotBlank @Size(min = 2, max = 64) private String title;
    @NotBlank @Size(min = 4, max = 2048) private String content;
    private Set<Long> markerIds = new HashSet<>();
}