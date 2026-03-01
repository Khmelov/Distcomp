package com.example.demo.service;

import com.example.demo.dto.requests.AuthorRequestTo;
import com.example.demo.dto.responses.AuthorResponseTo;
import com.github.dockerjava.api.exception.NotFoundException;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.crossstore.ChangeSetPersister;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.transaction.annotation.Transactional;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import static org.junit.Assert.*;

@Testcontainers
@SpringBootTest
@Transactional
public class AuthorServiceTest {
    @Container
    static PostgreSQLContainer<?> postgres =
            new PostgreSQLContainer<>("postgres:15")
                    .withDatabaseName("test")
                    .withUsername("postgres")
                    .withPassword("postgres");

    @DynamicPropertySource
    static void configure(DynamicPropertyRegistry registry) {
        registry.add("spring.datasource.url", postgres::getJdbcUrl);
        registry.add("spring.datasource.username", postgres::getUsername);
        registry.add("spring.datasource.password", postgres::getPassword);
    }

    @Autowired
    private AuthorService authorService;

    @Test
    void createAuthor_shouldSaveAndResponseTo() {
        AuthorRequestTo request = new AuthorRequestTo();
        request.setLogin("test@mail.com");
        request.setPassword("password123");
        request.setFirstname("Test");
        request.setLastname("User");

        AuthorResponseTo response = authorService.create(request);

        assertNotNull(response.getId());
        assertEquals("test@mail.com", response.getLogin());
    }

    @Test
    void getById_whenNotExists_shouldThrowException() {
        assertThrows(ChangeSetPersister.NotFoundException.class,
                () -> authorService.findById(999L));
    }

    @Test
    void deleteAuthor_shouldRemoveEntity() {
        AuthorRequestTo request = new AuthorRequestTo();
        request.setLogin("delete@mail.com");
        request.setPassword("password123");
        request.setFirstname("Delete");
        request.setLastname("User");

        AuthorResponseTo response = authorService.create(request);

        authorService.delete(response.getId());

        assertThrows(NotFoundException.class,
                () -> authorService.findById(response.getId()));
    }
}
