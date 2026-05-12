package com.lizaveta.notebook.security;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.web.access.AccessDeniedHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
public class RestAccessDeniedHandler implements AccessDeniedHandler {

    private static final int ERROR_CODE = 40301;
    private final RestSecurityErrorWriter errorWriter;

    public RestAccessDeniedHandler(final RestSecurityErrorWriter errorWriter) {
        this.errorWriter = errorWriter;
    }

    @Override
    public void handle(
            final HttpServletRequest request,
            final HttpServletResponse response,
            final AccessDeniedException accessDeniedException) throws IOException {
        errorWriter.writeError(
                response,
                HttpServletResponse.SC_FORBIDDEN,
                accessDeniedException.getMessage() == null
                        ? "Access is denied"
                        : accessDeniedException.getMessage(),
                ERROR_CODE);
    }
}
