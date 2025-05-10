package com.example.demo.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CreatorResponseTo {
    private Long id;
    private String firstname;
    private String lastname;
    private String login;
}
