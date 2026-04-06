package com.writerservice.sevices;

import com.writerservice.configs.exceptionhandlerconfig.exceptions.LoginAlreadyExistsException;
import com.writerservice.configs.exceptionhandlerconfig.exceptions.UserNotFoundException;
import com.writerservice.configs.exceptionhandlerconfig.exceptions.WriterNotFoundException;
import com.writerservice.configs.tweetclientconfig.TweetClient;
import com.writerservice.dtos.WriterRequestTo;
import com.writerservice.dtos.WriterResponseTo;
import com.writerservice.models.Writer;
import com.writerservice.repositories.WriterRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class WriterService {

    private final WriterRepository writerRepository;
    private final PasswordEncoder encoder;
    private final TweetClient tweetClient;

    public WriterResponseTo createWriter(WriterRequestTo request) {
        if (writerRepository.findByLogin(request.getLogin()).isPresent()) {
            throw new LoginAlreadyExistsException("Login already exists");
        }
        Writer saved = writerRepository.save(toEntity(request));
        return toDto(saved);
    }

    public WriterResponseTo findWriterById(Long id) {
        Writer writer = writerRepository.findById(id)
                .orElseThrow(() -> new UserNotFoundException("User not found"));
        return toDto(writer);
    }

    public List<WriterResponseTo> findAllWriters() {
        return writerRepository.findAll().stream()
                .map(this::toDto)
                .toList();
    }

    public WriterResponseTo updateProfile(WriterRequestTo request, Long id) {
        Writer writer = writerRepository.findById(id)
                .orElseThrow(() -> new UserNotFoundException("User not found"));

        if (writerRepository.existsByLoginAndIdNot(request.getLogin(), id)) {
            throw new LoginAlreadyExistsException("Login already exists");
        }

        writer.setLogin(request.getLogin());
        writer.setFirstname(request.getFirstname());
        writer.setLastname(request.getLastname());
        writer.setPassword(encoder.encode(request.getPassword()));

        Writer saved = writerRepository.save(writer);
        return toDto(saved);
    }

    @Transactional
    public void deleteWriter(Long id) {
        if (!writerRepository.existsById(id)) {
            throw new WriterNotFoundException("Writer not found");
        }

        tweetClient.deleteTweetsByWriterId(id);
        writerRepository.deleteById(id);
    }

    private Writer toEntity(WriterRequestTo request) {
        return Writer.builder()
                .login(request.getLogin())
                .password(encoder.encode(request.getPassword()))
                .firstname(request.getFirstname())
                .lastname(request.getLastname())
                .build();
    }

    private WriterResponseTo toDto(Writer entity) {
        return WriterResponseTo.builder()
                .id(entity.getId())
                .login(entity.getLogin())
                .firstname(entity.getFirstname())
                .lastname(entity.getLastname())
                .build();
    }
}
