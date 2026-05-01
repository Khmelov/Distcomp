package by.bsuir.task320.dto.response;

public record ErrorResponse(
        String errorMessage,
        Integer errorCode
) {
}
