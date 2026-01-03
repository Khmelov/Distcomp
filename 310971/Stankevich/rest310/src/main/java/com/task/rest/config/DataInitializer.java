package com.task.rest.config;

import com.task.rest.dto.AuthorRequestTo;
import com.task.rest.service.AuthorService;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class DataInitializer {

    private final AuthorService authorService;

    @PostConstruct
    public void init() {
        log.info("Initializing default data...");

        AuthorRequestTo firstAuthor = AuthorRequestTo.builder()
                .login("i.stankewich@icloud.com")
                .password("password12345")
                .firstname("Илья")
                .lastname("Станкевич")
                .build();

        authorService.create(firstAuthor);
        log.info("Default author created: {}", firstAuthor.getLogin());
    }
}