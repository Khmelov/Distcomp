package com.sergey.orsik.dto.request;

import com.fasterxml.jackson.annotation.JsonRootName;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;

@Data
@NoArgsConstructor
@AllArgsConstructor
@JsonRootName("tweet")
public class TweetRequestTo {

    private Long id;
    private Long creatorId;
    private String title;
    private String content;
    private Instant created;
    private Instant modified;
}
