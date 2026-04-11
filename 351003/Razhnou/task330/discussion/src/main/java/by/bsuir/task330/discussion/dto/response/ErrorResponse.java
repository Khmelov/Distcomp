package by.bsuir.task330.discussion.dto.response;

public record ErrorResponse(
        String errorMessage,
        Integer errorCode
) {
}
