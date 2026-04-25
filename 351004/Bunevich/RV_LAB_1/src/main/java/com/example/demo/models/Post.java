package com.example.demo.models;

import lombok.Data;

@Data
public class Post extends BaseEntity{
    public long storyId;
    public String content;
}
