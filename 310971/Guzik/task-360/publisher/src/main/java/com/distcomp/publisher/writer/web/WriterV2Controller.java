package com.distcomp.publisher.writer.web;

import com.distcomp.publisher.exception.DuplicateResourceException;
import com.distcomp.publisher.writer.domain.Writer;
import com.distcomp.publisher.writer.domain.WriterRole;
import com.distcomp.publisher.writer.dto.WriterRegistrationRequest;
import com.distcomp.publisher.writer.dto.WriterResponseV2;
import com.distcomp.publisher.writer.repo.WriterRepository;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v2.0/writers")
public class WriterV2Controller {

    private final WriterRepository writerRepository;
    private final PasswordEncoder passwordEncoder;

    public WriterV2Controller(WriterRepository writerRepository, PasswordEncoder passwordEncoder) {
        this.writerRepository = writerRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @PostMapping
    public ResponseEntity<WriterResponseV2> register(@Valid @RequestBody WriterRegistrationRequest request) {
        if (writerRepository.existsByLogin(request.getLogin())) {
            throw new DuplicateResourceException("Writer with login '" + request.getLogin() + "' already exists");
        }

        Writer writer = new Writer();
        writer.setLogin(request.getLogin());
        writer.setPassword(passwordEncoder.encode(request.getPassword()));
        writer.setFirstname(request.getFirstname());
        writer.setLastname(request.getLastname());
        writer.setRole(request.getRole() != null ? request.getRole() : WriterRole.CUSTOMER);

        Writer saved = writerRepository.save(writer);

        WriterResponseV2 response = new WriterResponseV2(
                saved.getId() != null ? saved.getId() : 0,
                saved.getLogin(),
                saved.getFirstname(),
                saved.getLastname(),
                saved.getRole()
        );

        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }
}
