package com.tweetservice.dtos.marker;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class MarkerResponseTo {

    private Long id;

    private String name;

}
