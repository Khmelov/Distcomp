package by.bsuir.publisher.exceptions;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

@SuperBuilder
@NoArgsConstructor
@Data
public class ErrorDto {
    private String errorMessage;
    private String errorCode;
}
