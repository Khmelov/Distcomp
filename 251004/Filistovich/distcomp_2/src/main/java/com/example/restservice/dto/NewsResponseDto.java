package com.example.restservice.dto;

import lombok.Data;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

@Data
public class NewsResponseDto implements Serializable {
    private Long id;
    private String title;
    private String content;
    private Date created;
    private Date modified;
    private Long authorId;
    private List<MarkerResponseDto> markers;
}
