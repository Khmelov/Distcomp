package com.blog.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class TagResponseTo {
    private Long id;
    private String name;
    private LocalDateTime created;
    private LocalDateTime modified;
}