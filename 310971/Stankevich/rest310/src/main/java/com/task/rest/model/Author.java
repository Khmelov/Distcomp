package com.task.rest.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Author {
    private Long id;
    private String login;
    private String password;
    private String firstname;
    private String lastname;
}