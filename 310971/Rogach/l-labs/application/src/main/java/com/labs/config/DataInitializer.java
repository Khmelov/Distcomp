package com.labs.config;

import com.labs.domain.entity.Writer;
import com.labs.domain.repository.WriterRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class DataInitializer implements CommandLineRunner {
    private final WriterRepository writerRepository;

    @Override
    public void run(String... args) {
        if (writerRepository.findByLogin("rogach.lizaveta@gmail.com").isEmpty()) {
            Writer writer = Writer.builder()
                    .login("rogach.lizaveta@gmail.com")
                    .password("password123")
                    .firstname("Елизавета")
                    .lastname("Рогач")
                    .build();
            writerRepository.save(writer);
            log.info("Initial writer created: {}", writer.getLogin());
        }
    }
}

