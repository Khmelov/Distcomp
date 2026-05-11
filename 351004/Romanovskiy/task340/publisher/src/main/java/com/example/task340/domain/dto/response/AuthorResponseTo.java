package com.example.task340.domain.dto.response;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class AuthorResponseTo {
    private Long id;
    private String login;
    private String firstname;
    private String lastname;
}