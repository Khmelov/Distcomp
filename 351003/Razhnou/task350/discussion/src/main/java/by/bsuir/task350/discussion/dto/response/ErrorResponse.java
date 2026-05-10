package by.bsuir.task350.discussion.dto.response;

public record ErrorResponse(
        String errorMessage,
        Integer errorCode
) {
}
