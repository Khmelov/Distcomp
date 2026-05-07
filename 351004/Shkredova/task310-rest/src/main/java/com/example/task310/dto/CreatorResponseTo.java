package com.example.task310.dto;

import lombok.Data;
import java.time.LocalDateTime;

@Data
public class CreatorResponseTo {
    private Long id;
    private String login;
    private String firstname;
    private String lastname;
    private LocalDateTime created;
    private LocalDateTime modified;
}