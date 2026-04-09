package com.tweetmarkersservice.models;

import jakarta.persistence.*;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "tbl_tweet_markers",
        indexes = {
                @Index(name = "idx_tweet_id", columnList = "tweetId"),
                @Index(name = "idx_marker_id", columnList = "markerId")
        })
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@IdClass(TweetMarkerId.class)
public class TweetMarkers {

    @Id
    @Column(name = "tweet_id", nullable = false)
    private Long tweetId;

    @Id
    @Column(name = "marker_id", nullable = false)
    private Long markerId;
}

