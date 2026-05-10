package by.bsuir.task340.discussion.dto.response;

public record ErrorResponse(
        String errorMessage,
        Integer errorCode
) {
}
