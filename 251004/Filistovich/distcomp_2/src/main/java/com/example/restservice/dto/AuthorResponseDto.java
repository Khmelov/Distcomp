package com.example.restservice.dto;

import lombok.Data;

import java.io.Serializable;

@Data
public class AuthorResponseDto implements Serializable {
    private Long id;
    private String login;
    private String firstname;
    private String lastname;
}
