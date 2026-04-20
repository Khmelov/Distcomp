package by.bsuir.task310.dto.response;

public record ErrorResponse(
        String errorMessage,
        Integer errorCode
) {
}
