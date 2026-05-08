package com.sergey.orsik.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CommentRequestTo {

    private Long id;

    @NotNull(message = "tweetId must not be null")
    private Long tweetId;

    @NotBlank(message = "content must not be blank")
    @Size(min = 2, max = 2048)
    private String content;

    private Instant created;
}
