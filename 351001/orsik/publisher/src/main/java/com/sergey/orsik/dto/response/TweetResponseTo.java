package com.sergey.orsik.dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class TweetResponseTo {

    private Long id;
    private Long creatorId;
    private String title;
    private String content;
    private Instant created;
    private Instant modified;
}
