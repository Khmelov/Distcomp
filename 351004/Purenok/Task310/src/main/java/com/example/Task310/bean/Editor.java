package com.example.Task310.bean;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Editor {
    private Long id;
    private String login;
    private String password;
    private String firstname;
    private String lastname;
}