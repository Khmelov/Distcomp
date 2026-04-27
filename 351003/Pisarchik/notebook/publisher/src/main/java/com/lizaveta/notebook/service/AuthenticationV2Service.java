package com.lizaveta.notebook.service;

import com.lizaveta.notebook.exception.UnauthorizedException;
import com.lizaveta.notebook.model.dto.request.LoginRequestTo;
import com.lizaveta.notebook.model.dto.request.WriterRegistrationV2To;
import com.lizaveta.notebook.model.dto.request.WriterRequestTo;
import com.lizaveta.notebook.model.dto.response.AccessTokenResponseTo;
import com.lizaveta.notebook.model.dto.response.WriterResponseTo;
import com.lizaveta.notebook.model.entity.Writer;
import com.lizaveta.notebook.repository.WriterRepository;
import com.lizaveta.notebook.security.JwtService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class AuthenticationV2Service {

    private static final int INVALID_CREDENTIALS_CODE = 40102;
    private final WriterRepository writerRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final WriterService writerService;

    public AuthenticationV2Service(
            final WriterRepository writerRepository,
            final PasswordEncoder passwordEncoder,
            final JwtService jwtService,
            final WriterService writerService) {
        this.writerRepository = writerRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtService = jwtService;
        this.writerService = writerService;
    }

    public AccessTokenResponseTo login(final LoginRequestTo request) {
        Writer writer = writerRepository.findByLogin(request.login())
                .orElseThrow(
                        () -> new UnauthorizedException("Invalid login or password", INVALID_CREDENTIALS_CODE));
        if (!passwordEncoder.matches(request.password(), writer.getPassword())) {
            throw new UnauthorizedException("Invalid login or password", INVALID_CREDENTIALS_CODE);
        }
        String accessToken = jwtService.buildAccessToken(writer.getLogin(), writer.getRole());
        return new AccessTokenResponseTo(accessToken, "Bearer");
    }

    public WriterResponseTo register(final WriterRegistrationV2To request) {
        String roleOrNull = request.role();
        if (roleOrNull != null && roleOrNull.isBlank()) {
            roleOrNull = null;
        }
        WriterRequestTo mapped = new WriterRequestTo(
                request.login(),
                request.password(),
                request.firstName(),
                request.lastName(),
                roleOrNull);
        return writerService.create(mapped);
    }
}
