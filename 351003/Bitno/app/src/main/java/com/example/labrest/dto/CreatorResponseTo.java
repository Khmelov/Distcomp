package com.example.labrest.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import jakarta.validation.constraints.*;
@Data @NoArgsConstructor @AllArgsConstructor
public class CreatorResponseTo {
    private Long id;
    private String login;
    private String firstname;
    private String lastname;
}