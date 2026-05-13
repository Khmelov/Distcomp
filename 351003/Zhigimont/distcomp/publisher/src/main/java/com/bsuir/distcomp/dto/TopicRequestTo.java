package com.bsuir.distcomp.dto;


import jakarta.validation.constraints.Size;
import lombok.Data;

import java.util.List;

@Data
public class TopicRequestTo {

    private Long writerId;

    @Size(min = 2, max = 64)
    private String title;

    @Size(min = 4, max = 2048)
    private String content;

    private List<String> markers;

}
