package by.bsuir.distcomp.security;

import by.bsuir.distcomp.cache.RedisCacheService;
import by.bsuir.distcomp.dto.WriterDto;
import by.bsuir.distcomp.dto.auth.LoginRequest;
import by.bsuir.distcomp.dto.auth.LoginResponse;
import by.bsuir.distcomp.exception.ApiException;
import by.bsuir.distcomp.model.UserRole;
import by.bsuir.distcomp.model.Writer;
import by.bsuir.distcomp.repository.WriterRepository;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class AuthService {
    private final WriterRepository writerRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final RedisCacheService cache;

    public AuthService(
            WriterRepository writerRepository,
            PasswordEncoder passwordEncoder,
            JwtService jwtService,
            RedisCacheService cache) {
        this.writerRepository = writerRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtService = jwtService;
        this.cache = cache;
    }

    @Transactional
    public WriterDto register(WriterDto dto) {
        if (writerRepository.existsByLogin(dto.login())) {
            throw new ApiException(HttpStatus.FORBIDDEN, "40301", "Writer login already exists");
        }
        Writer writer = new Writer();
        writer.setLogin(dto.login());
        writer.setPassword(passwordEncoder.encode(dto.password()));
        writer.setFirstname(dto.firstname());
        writer.setLastname(dto.lastname());
        writer.setRole(dto.role() == null ? UserRole.CUSTOMER : dto.role());
        Writer saved = writerRepository.save(writer);
        WriterDto response = toDto(saved);
        cache.put("writer:" + saved.getId(), response);
        cache.evictByPrefix("writer:list:");
        return response;
    }

    @Transactional(readOnly = true)
    public LoginResponse login(LoginRequest request) {
        Writer writer = writerRepository.findByLogin(request.login())
                .orElseThrow(() -> new ApiException(HttpStatus.UNAUTHORIZED, "40102", "Invalid credentials"));
        if (!passwordEncoder.matches(request.password(), writer.getPassword()) && !request.password().equals(writer.getPassword())) {
            throw new ApiException(HttpStatus.UNAUTHORIZED, "40102", "Invalid credentials");
        }
        return new LoginResponse(jwtService.generate(writer.getLogin(), writer.getRole()), "Bearer");
    }

    @Transactional(readOnly = true)
    public Writer current(String login) {
        return writerRepository.findByLogin(login)
                .orElseThrow(() -> new ApiException(HttpStatus.UNAUTHORIZED, "40103", "Current user not found"));
    }

    private WriterDto toDto(Writer writer) {
        return new WriterDto(writer.getId(), writer.getLogin(), writer.getPassword(),
                writer.getFirstname(), writer.getLastname(), writer.getRole());
    }
}
