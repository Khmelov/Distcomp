package com.example.rest.dto.requestDto;

import jakarta.validation.constraints.Size;
import lombok.Data;

import java.util.List;

@Data
public class StoryRequestTo {

    private long userId;

    @Size(min = 2, max = 64)
    private String title;

    @Size(min = 4, max = 2048)
    private String content;


    private List<String> labels;
}
