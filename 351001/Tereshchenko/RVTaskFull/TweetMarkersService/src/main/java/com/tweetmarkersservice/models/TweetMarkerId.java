package com.tweetmarkersservice.models;

import jakarta.persistence.Embeddable;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Embeddable
@Data
@NoArgsConstructor
@AllArgsConstructor
public class TweetMarkerId implements Serializable {
    private Long tweetId;
    private Long markerId;
}