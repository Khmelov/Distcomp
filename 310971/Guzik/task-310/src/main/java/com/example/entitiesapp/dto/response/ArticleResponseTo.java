package com.example.entitiesapp.dto.response;

import com.example.entitiesapp.dto.BaseDto;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ArticleResponseTo extends BaseDto {
    private Long writerId;
    private String title;
    private String content;
}