package com.example.demo.service;

import com.example.demo.dto.requests.AuthorRequestTo;
import com.example.demo.dto.responses.AuthorResponseTo;
import com.github.dockerjava.api.exception.NotFoundException;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.crossstore.ChangeSetPersister;
import org.springframework.transaction.annotation.Transactional;

import static org.junit.Assert.*;

@SpringBootTest
@Transactional
public class AuthorServiceTest {

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
    void deleteAuthor_shouldRemoveEntity() throws ChangeSetPersister.NotFoundException {
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
