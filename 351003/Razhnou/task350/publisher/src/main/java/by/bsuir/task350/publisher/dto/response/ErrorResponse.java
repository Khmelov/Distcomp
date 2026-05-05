package by.bsuir.task350.publisher.dto.response;

public record ErrorResponse(
        String errorMessage,
        Integer errorCode
) {
}
