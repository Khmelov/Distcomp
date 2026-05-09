package by.bsuir.task330.publisher.dto.response;

public record ErrorResponse(
        String errorMessage,
        Integer errorCode
) {
}
