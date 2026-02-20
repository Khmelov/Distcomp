package org.example.newsapi.entity;

import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
public class User extends BaseEntity {
    private String login;
    private String password;
    private String firstname;
    private String lastname;
}