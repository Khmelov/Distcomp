package com.tweetservice.dtos;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class TweetResponseTo {

    private Long id;

    private Long writerId;

    private String title;

    private String content;

    private LocalDateTime created;

}
