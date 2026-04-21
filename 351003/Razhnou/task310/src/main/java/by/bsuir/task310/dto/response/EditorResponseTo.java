package by.bsuir.task310.dto.response;

public record EditorResponseTo(
        Long id,
        String login,
        String password,
        String firstname,
        String lastname
) {
}
