package com.example.demo.dto.requests;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Value;

import java.time.ZonedDateTime;

@Value
public class IssueRequestTo {
    private String title;
    private String content;
    private ZonedDateTime created;
    private ZonedDateTime modified;
}
