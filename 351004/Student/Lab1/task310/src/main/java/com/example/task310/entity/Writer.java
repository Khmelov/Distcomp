package com.example.task310.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class Writer {
    private Long id;
    private String login;
    private String password;
    private String firstname;
    private String lastname;
}