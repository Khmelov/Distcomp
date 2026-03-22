package com.example.demo.dto.requests;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class MessageRequestTo {
    @NotNull
    private Long issueId;
    @NotNull
    private Long id;
    @NotBlank
    @Size(min = 2, max = 2048)
    private String content;
}

