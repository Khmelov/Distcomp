package com.task.rest.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class NoticeRequestTo {

    private Long id;

    @Size(min = 2, max = 64)
    private String country;

    @NotNull
    private Long tweetId;

    @NotBlank
    @Size(min = 2, max = 2048)
    private String content;
}
