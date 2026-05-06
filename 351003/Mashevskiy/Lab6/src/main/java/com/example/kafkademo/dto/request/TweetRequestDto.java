package com.example.kafkademo.dto.request;

import lombok.Data;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import java.util.List;

@Data
public class TweetRequestDto {
    @NotBlank
    @Size(min = 1, max = 2000)
    private String content;

    private Long creatorId;
    private List<Long> markerIds;
}