package com.lizaveta.notebook.config;

import com.lizaveta.notebook.repository.entity.WriterEntity;
import com.lizaveta.notebook.repository.jpa.WriterJpaRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
public class PlainPasswordUpgradeRunner implements ApplicationRunner {

    private static final Logger LOG = LoggerFactory.getLogger(PlainPasswordUpgradeRunner.class);

    private final WriterJpaRepository writerJpaRepository;
    private final PasswordEncoder passwordEncoder;

    public PlainPasswordUpgradeRunner(
            final WriterJpaRepository writerJpaRepository,
            final PasswordEncoder passwordEncoder) {
        this.writerJpaRepository = writerJpaRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    @Transactional
    public void run(final ApplicationArguments args) {
        for (WriterEntity writer : writerJpaRepository.findAll()) {
            if (isAlreadyBcryptEncoded(writer.getPassword())) {
                continue;
            }
            String encoded = passwordEncoder.encode(writer.getPassword());
            writer.setPassword(encoded);
            writerJpaRepository.save(writer);
            LOG.info("Migrated password encoding for writer login={}", writer.getLogin());
        }
    }

    private static boolean isAlreadyBcryptEncoded(final String password) {
        return password != null
                && (password.startsWith("$2a$") || password.startsWith("$2b$") || password.startsWith("$2y$"));
    }
}
