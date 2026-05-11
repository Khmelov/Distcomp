package com.example.demo.config;

import com.example.demo.entity.Author;
import com.example.demo.repository.AuthorRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Profile;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import java.util.List;

@Component
@Profile("docker")
public class PasswordMigrationRunner implements CommandLineRunner {

    @Autowired
    private AuthorRepository authorRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Override
    public void run(String... args) {
        List<Author> authors = authorRepository.findAll();
        for (Author author : authors) {
            if (author.getPassword() != null && !author.getPassword().startsWith("$2a$")) {
                author.setPassword(passwordEncoder.encode(author.getPassword()));
                authorRepository.save(author);
            }
        }
    }
}