package com.example.entitiesapp.dto.response;

import com.example.entitiesapp.dto.BaseDto;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class PostResponseTo extends BaseDto {
    private Long articleId;
    private String content;
}