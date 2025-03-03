package com.example.lab1.entity;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import lombok.*;

@Getter
@Setter
public class User implements BaseEntity {

    private Long id;

    private String login;

    private String password;

    private String firstname;

    private String lastname;
}
