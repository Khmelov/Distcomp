package com.sergey.orsik.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Comment implements Identifiable {

    private Long id;
    private Long tweetId;
    private String content;
}
