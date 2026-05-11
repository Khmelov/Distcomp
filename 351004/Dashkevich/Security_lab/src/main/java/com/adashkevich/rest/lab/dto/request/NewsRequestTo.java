package com.adashkevich.rest.lab.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.util.Set;


public class NewsRequestTo {
    @NotNull
    public Long editorId;

    @NotBlank @Size(min = 2, max = 64)
    public String title;

    @NotBlank @Size(min = 4, max = 2048)
    public String content;

    public Set<Long> markerIds;
}
