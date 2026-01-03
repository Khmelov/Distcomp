package com.task.rest.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Tweet {
    private Long id;
    private Long authorId;
    private String title;
    private String content;
    private LocalDateTime created;
    private LocalDateTime modified;

    @Builder.Default
    private Set<Long> markIds = new HashSet<>();
}