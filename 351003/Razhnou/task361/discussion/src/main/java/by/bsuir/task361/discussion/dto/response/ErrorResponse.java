package by.bsuir.task361.discussion.dto.response;

public record ErrorResponse(
        String errorMessage,
        Integer errorCode
) {
}
