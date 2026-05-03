package by.bsuir.task310.model;

import lombok.Data;

@Data
public class Author {
    private Long id;
    private String login;
    private String password;
    private String firstname;
    private String lastname;
}