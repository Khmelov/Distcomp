package by.bsuir.task310.dto;

import lombok.Data;

@Data
public class AuthorRequestTo {
    private Long id;
    private String login;
    private String password;
    private String firstname;
    private String lastname;
}