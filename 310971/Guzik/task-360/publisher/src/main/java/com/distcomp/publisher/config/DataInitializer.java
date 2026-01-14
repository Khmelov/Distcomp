package com.distcomp.publisher.config;

import com.distcomp.publisher.writer.domain.Writer;
import com.distcomp.publisher.writer.domain.WriterRole;
import com.distcomp.publisher.writer.repo.WriterRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@Component
public class DataInitializer implements CommandLineRunner {

    private final WriterRepository writerRepository;
    private final PasswordEncoder passwordEncoder;

    public DataInitializer(WriterRepository writerRepository, PasswordEncoder passwordEncoder) {
        this.writerRepository = writerRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public void run(String... args) {
        if (writerRepository.count() > 0) {
            return;
        }

        Writer writer = new Writer();
        writer.setLogin("aleksandraguzik32@gmail.com");
        writer.setPassword(passwordEncoder.encode("qwerty123"));
        writer.setFirstname("Александра");
        writer.setLastname("Гузик");
        writer.setRole(WriterRole.ADMIN);
        writerRepository.save(writer);
    }
}
