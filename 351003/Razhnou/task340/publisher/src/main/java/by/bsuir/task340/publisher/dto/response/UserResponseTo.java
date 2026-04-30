package by.bsuir.task340.publisher.dto.response;

public record UserResponseTo(
        Long id,
        String login,
        String password,
        String firstname,
        String lastname
) {
}
