package by.bsuir.task310.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class ErrorResponseTo {
    private String errorMessage;
    private String errorCode;
}