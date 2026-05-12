package com.bsuir.distcomp.dto;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class TopicResponseTo {

    private Long id;
    private Long writerId;
    private String title;
    private String content;
    private LocalDateTime created;
    private LocalDateTime modified;

}

