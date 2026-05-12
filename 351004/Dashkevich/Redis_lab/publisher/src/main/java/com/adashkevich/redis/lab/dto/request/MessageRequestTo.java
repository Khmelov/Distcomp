package com.adashkevich.redis.lab.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;


public class MessageRequestTo {
    @NotNull
    public Long newsId;

    @NotBlank @Size(min = 2, max = 2048)
    public String content;
}
