package com.example.demo.models;

import java.time.LocalDateTime;

public class Story extends BaseEntity{
    public long writerId;
    public String title;
    public String content;
    public LocalDateTime created;
    public LocalDateTime modified;

}
