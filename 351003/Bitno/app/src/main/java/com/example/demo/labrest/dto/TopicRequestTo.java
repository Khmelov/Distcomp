package com.example.demo.labrest.dto;

import jakarta.validation.constraints.*;
import lombok.*;
import java.util.Set;

@Data @NoArgsConstructor @AllArgsConstructor
public class TopicRequestTo {
    @NotNull private Long creatorId;
    @NotBlank @Size(min = 2, max = 64) private String title;
    @NotBlank @Size(min = 4, max = 2048) private String content;
    private Set<Long> markerIds = Set.of();
    private Set<String> markers = Set.of();
}