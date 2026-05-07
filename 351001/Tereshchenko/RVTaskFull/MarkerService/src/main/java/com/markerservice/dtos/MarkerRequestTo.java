package com.markerservice.dtos;

import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MarkerRequestTo {

    private Long tweetId;

    @Size(min = 2, max = 32)
    private String name;
}
