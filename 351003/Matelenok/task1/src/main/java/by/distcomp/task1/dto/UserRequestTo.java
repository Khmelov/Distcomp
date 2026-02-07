package by.distcomp.task1.dto;

public record UserRequestTo(
        String login,
        String password,
        String firstname,
        String lastname
) { }
