package com.example.demo.labrest.dto;

import lombok.*;

@Data @NoArgsConstructor @AllArgsConstructor
public class CreatorResponseTo {
    private Long id;
    private String login;
    private String firstname;
    private String lastname;
}