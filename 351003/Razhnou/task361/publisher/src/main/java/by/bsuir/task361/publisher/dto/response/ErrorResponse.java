package by.bsuir.task361.publisher.dto.response;

public record ErrorResponse(
        String errorMessage,
        Integer errorCode
) {
}
