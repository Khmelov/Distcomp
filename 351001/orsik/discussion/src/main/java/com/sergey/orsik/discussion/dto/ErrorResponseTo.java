package com.sergey.orsik.discussion.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ErrorResponseTo {

    private Instant timestamp;
    private String errorCode;
    private String errorMessage;
    private String path;
    private List<FieldErrorTo> errors;

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class FieldErrorTo {
        private String field;
        private String errorCode;
        private String errorMessage;
    }
}
