package com.lizaveta.notebook.security;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
public class RestAuthenticationEntryPoint implements AuthenticationEntryPoint {

    private static final int ERROR_CODE = 40101;
    private final RestSecurityErrorWriter errorWriter;

    public RestAuthenticationEntryPoint(final RestSecurityErrorWriter errorWriter) {
        this.errorWriter = errorWriter;
    }

    @Override
    public void commence(
            final HttpServletRequest request,
            final HttpServletResponse response,
            final AuthenticationException authException) throws IOException {
        errorWriter.writeError(
                response,
                HttpServletResponse.SC_UNAUTHORIZED,
                "Authentication required: use Authorization: Bearer <access_token> for v2.0",
                ERROR_CODE);
    }
}
