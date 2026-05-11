package org.polozkov.dto.error;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@AllArgsConstructor
public class ErrorResponseDtoOut {

    private int status;

    private String error;

    private String message;

    private LocalDateTime timestamp;

}
