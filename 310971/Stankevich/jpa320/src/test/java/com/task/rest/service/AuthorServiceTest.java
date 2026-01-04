package com.task.rest.service;

import com.task.rest.dto.AuthorRequestTo;
import com.task.rest.dto.AuthorResponseTo;
import com.task.rest.exception.ResourceNotFoundException;
import com.task.rest.model.Author;
import com.task.rest.repository.AuthorRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AuthorServiceTest {

    @Mock
    private AuthorRepository authorRepository;

    @InjectMocks
    private AuthorService authorService;

    private Author author;
    private AuthorRequestTo requestTo;

    @BeforeEach
    void setUp() {
        author = new Author();
        author.setId(1L);
        author.setLogin("testuser");
        author.setPassword("password123");
        author.setFirstname("John");
        author.setLastname("Doe");

        requestTo = new AuthorRequestTo();
        requestTo.setLogin("testuser");
        requestTo.setPassword("password123");
        requestTo.setFirstname("John");
        requestTo.setLastname("Doe");
    }

    @Test
    void testGetById_Success() {
        when(authorRepository.findById(1L)).thenReturn(Optional.of(author));

        AuthorResponseTo response = authorService.getById(1L);

        assertNotNull(response);
        assertEquals(1L, response.getId());
        assertEquals("testuser", response.getLogin());
        verify(authorRepository, times(1)).findById(1L);
    }

    @Test
    void testGetById_NotFound() {
        when(authorRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> authorService.getById(1L));
        verify(authorRepository, times(1)).findById(1L);
    }

    @Test
    void testCreate_Success() {
        when(authorRepository.existsByLogin("testuser")).thenReturn(false);
        when(authorRepository.save(any(Author.class))).thenReturn(author);

        AuthorResponseTo response = authorService.create(requestTo);

        assertNotNull(response);
        assertEquals("testuser", response.getLogin());
        verify(authorRepository, times(1)).existsByLogin("testuser");
        verify(authorRepository, times(1)).save(any(Author.class));
    }

    @Test
    void testCreate_DuplicateLogin() {
        when(authorRepository.existsByLogin("testuser")).thenReturn(true);

        ResponseStatusException exception = assertThrows(
                ResponseStatusException.class,
                () -> authorService.create(requestTo)
        );

        assertEquals(HttpStatus.FORBIDDEN, exception.getStatusCode());
        verify(authorRepository, times(1)).existsByLogin("testuser");
        verify(authorRepository, never()).save(any(Author.class));
    }

    @Test
    void testUpdate_Success() {
        when(authorRepository.findById(1L)).thenReturn(Optional.of(author));
        when(authorRepository.findByLogin("testuser")).thenReturn(Optional.of(author));
        when(authorRepository.save(any(Author.class))).thenReturn(author);

        AuthorResponseTo response = authorService.update(1L, requestTo);

        assertNotNull(response);
        verify(authorRepository, times(1)).findById(1L);
        verify(authorRepository, times(1)).save(any(Author.class));
    }

    @Test
    void testDelete_Success() {
        when(authorRepository.existsById(1L)).thenReturn(true);
        doNothing().when(authorRepository).deleteById(1L);

        assertDoesNotThrow(() -> authorService.delete(1L));
        verify(authorRepository, times(1)).existsById(1L);
        verify(authorRepository, times(1)).deleteById(1L);
    }

    @Test
    void testDelete_NotFound() {
        when(authorRepository.existsById(1L)).thenReturn(false);

        assertThrows(ResourceNotFoundException.class, () -> authorService.delete(1L));
        verify(authorRepository, times(1)).existsById(1L);
        verify(authorRepository, never()).deleteById(any());
    }
}
