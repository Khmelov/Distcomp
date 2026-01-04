package com.example.entitiesapp.entities;

import lombok.Getter;
import lombok.Setter;
import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
public class Writer extends BaseEntity {
    private String login;
    private String password;
    private String firstName;
    private String lastName;
    private List<Article> articles = new ArrayList<>();
}