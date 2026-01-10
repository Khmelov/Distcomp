package com.blog.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class WriterResponseTo {
    private Long id;
    private String login;
    private String password;
    private String firstname;
    private String lastname;
    private LocalDateTime created;
    private LocalDateTime modified;
}