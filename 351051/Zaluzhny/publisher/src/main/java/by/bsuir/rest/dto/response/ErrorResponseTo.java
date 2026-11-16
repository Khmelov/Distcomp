package by.bsuir.rest.dto.response;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public class ErrorResponseTo {

    private final String errorMessage;
    private final Integer errorCode;
}
