package by.bsuir.task340.publisher.dto.response;

public record ErrorResponse(
        String errorMessage,
        Integer errorCode
) {
}
