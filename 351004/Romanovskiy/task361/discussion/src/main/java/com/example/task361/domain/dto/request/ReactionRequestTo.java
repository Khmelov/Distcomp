package com.example.task361.domain.dto.request;

import jakarta.validation.constraints.Size;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ReactionRequestTo {
    private Long id;

    @NotNull
    private Long tweetId;

    @NotNull
    @Size(min = 2, max = 2048)
    private String content;
    
    private String country;
    
    private String state;
}