package com.tweetservice.dtos;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.persistence.Column;
import jakarta.validation.constraints.Size;
import lombok.Data;

import java.util.List;

@Data
public class TweetRequestTo {

    private Long writerId;

    @Size(min = 2, max = 64)
    private String title;

    @Size(min = 4, max = 2048)
    private String content;

    @JsonProperty(value = "markers")
    private List<String> markerNames;
}
