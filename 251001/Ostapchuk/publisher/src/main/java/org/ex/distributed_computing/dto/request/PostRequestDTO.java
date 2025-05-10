package org.ex.distributed_computing.dto.request;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record PostRequestDTO(

    Long id,

    @NotNull
    Long tweetId,

    @NotNull
    @Size(min = 2, max = 2048)
    String content,

    String country
) {

}

