package com.sergey.orsik.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Tweet implements Identifiable {

    private Long id;
    private Long creatorId;
    private String title;
    private String content;
    private Instant created;
    private Instant modified;
}
