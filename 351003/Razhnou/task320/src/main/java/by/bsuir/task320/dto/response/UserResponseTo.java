package by.bsuir.task320.dto.response;

public record UserResponseTo(
        Long id,
        String login,
        String password,
        String firstname,
        String lastname
) {
}
