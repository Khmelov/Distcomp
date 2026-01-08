package com.task.rest.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class AuthorResponseTo {
    private Long id;
    private String login;
    private String firstname;
    private String lastname;
}
