package com.lizaveta.notebook.security;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.lizaveta.notebook.exception.ErrorResponse;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.nio.charset.StandardCharsets;

@Component
public class RestSecurityErrorWriter {

    private final ObjectMapper objectMapper;

    public RestSecurityErrorWriter(final ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }

    public void writeError(
            final HttpServletResponse response,
            final int status,
            final String errorMessage,
            final int errorCode) throws IOException {
        response.setStatus(status);
        response.setCharacterEncoding(StandardCharsets.UTF_8.name());
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        objectMapper.writeValue(
                response.getOutputStream(), new ErrorResponse(errorMessage, errorCode));
    }
}
