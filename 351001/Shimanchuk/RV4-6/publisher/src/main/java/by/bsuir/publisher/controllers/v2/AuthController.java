package by.bsuir.publisher.controllers.v2;

import by.bsuir.publisher.domain.Role;
import by.bsuir.publisher.domain.Writer;
import by.bsuir.publisher.dto.requests.v2.LoginRequestDto;
import by.bsuir.publisher.dto.requests.v2.WriterRegisterRequestDto;
import by.bsuir.publisher.dto.responses.WriterResponseDto;
import by.bsuir.publisher.dto.responses.v2.LoginResponseDto;
import by.bsuir.publisher.exceptions.EntityExistsException;
import by.bsuir.publisher.repositories.WriterRepository;
import by.bsuir.publisher.security.JwtService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v2.0")
@RequiredArgsConstructor
public class AuthController {

    private final WriterRepository writerRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final AuthenticationManager authenticationManager;

    @PostMapping("/writers")
    public ResponseEntity<WriterResponseDto> register(@RequestBody @Valid WriterRegisterRequestDto dto)
            throws EntityExistsException {
        if (writerRepository.findByLogin(dto.getLogin()).isPresent()) {
            throw new EntityExistsException("Writer with login " + dto.getLogin() + " already exists");
        }
        Role role = dto.getRole() != null ? dto.getRole() : Role.CUSTOMER;
        Writer writer = Writer.builder()
                .login(dto.getLogin())
                .password(passwordEncoder.encode(dto.getPassword()))
                .firstname(dto.getFirstname())
                .lastname(dto.getLastname())
                .role(role)
                .build();
        Writer saved = writerRepository.save(writer);
        return ResponseEntity.status(HttpStatus.CREATED).body(WriterResponseDto.builder()
                .id(saved.getId())
                .login(saved.getLogin())
                .firstname(saved.getFirstname())
                .lastname(saved.getLastname())
                .build());
    }

    @PostMapping("/login")
    public ResponseEntity<LoginResponseDto> login(@RequestBody @Valid LoginRequestDto dto) {
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(dto.getLogin(), dto.getPassword()));
        Writer writer = writerRepository.findByLogin(dto.getLogin()).orElseThrow();
        String token = jwtService.generate(writer.getLogin(), writer.getRole());
        return ResponseEntity.ok(LoginResponseDto.builder().accessToken(token).build());
    }
}
