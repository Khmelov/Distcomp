package org.ex.distributed_computing.dto.request;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record TweetRequestDTO(

    Long id,

    @NotNull
    Long userId,

    @NotNull
    @Size(min = 2, max = 64)
    String title,

    @NotNull
    @Size(min = 4, max = 2048)
    String content
) {

}
