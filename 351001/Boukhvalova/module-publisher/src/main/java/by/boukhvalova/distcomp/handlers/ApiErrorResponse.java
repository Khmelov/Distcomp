package by.boukhvalova.distcomp.handlers;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class ApiErrorResponse {
    private String errorMessage;
    private int errorCode;
}

