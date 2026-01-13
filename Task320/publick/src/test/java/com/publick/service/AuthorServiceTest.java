package com.publick.service;

import com.publick.dto.AuthorRequestTo;
import com.publick.dto.AuthorResponseTo;
import com.publick.entity.Author;
import com.publick.repository.AuthorRepository;
import com.publick.service.mapper.AuthorMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AuthorServiceTest {

    @Mock
    private AuthorRepository authorRepository;

    @Mock
    private AuthorMapper authorMapper;

    @InjectMocks
    private AuthorService authorService;

    private Author author;
    private AuthorRequestTo request;
    private AuthorResponseTo response;

    @BeforeEach
    void setUp() {
        author = new Author("test@example.com", "password123", "John", "Doe");
        author.setId(1L);

        request = new AuthorRequestTo();
        request.setLogin("test@example.com");
        request.setPassword("password123");
        request.setFirstname("John");
        request.setLastname("Doe");

        response = new AuthorResponseTo();
        response.setId(1L);
        response.setLogin("test@example.com");
        response.setPassword("password123");
        response.setFirstname("John");
        response.setLastname("Doe");
    }

    @Test
    void create_ShouldReturnCreatedAuthor() {
        // Given
        when(authorMapper.toEntity(request)).thenReturn(author);
        when(authorRepository.save(author)).thenReturn(author);
        when(authorMapper.toResponse(author)).thenReturn(response);

        // When
        AuthorResponseTo result = authorService.create(request);

        // Then
        assertNotNull(result);
        assertEquals(1L, result.getId());
        assertEquals("test@example.com", result.getLogin());
        verify(authorRepository).save(author);
        verify(authorMapper).toResponse(author);
    }

    @Test
    void getById_ShouldReturnAuthor_WhenExists() {
        // Given
        when(authorRepository.findById(1L)).thenReturn(Optional.of(author));
        when(authorMapper.toResponse(author)).thenReturn(response);

        // When
        AuthorResponseTo result = authorService.getById(1L);

        // Then
        assertNotNull(result);
        assertEquals(1L, result.getId());
        verify(authorRepository).findById(1L);
    }

    @Test
    void getById_ShouldThrowException_WhenNotExists() {
        // Given
        when(authorRepository.findById(1L)).thenReturn(Optional.empty());

        // When & Then
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
            () -> authorService.getById(1L));
        assertEquals("Author not found with id: 1", exception.getMessage());
    }

    @Test
    void getAll_ShouldReturnAllAuthors() {
        // Given
        List<Author> authors = Arrays.asList(author);
        when(authorRepository.findAll()).thenReturn(authors);
        when(authorMapper.toResponse(author)).thenReturn(response);

        // When
        List<AuthorResponseTo> result = authorService.getAll();

        // Then
        assertNotNull(result);
        assertEquals(1, result.size());
        verify(authorRepository).findAll();
    }

    @Test
    void getAllPaged_ShouldReturnPagedAuthors() {
        // Given
        Pageable pageable = PageRequest.of(0, 10);
        List<Author> authors = Arrays.asList(author);
        Page<Author> authorPage = new PageImpl<>(authors, pageable, 1);
        Page<AuthorResponseTo> responsePage = new PageImpl<>(Arrays.asList(response), pageable, 1);

        when(authorRepository.findAll(pageable)).thenReturn(authorPage);
        when(authorMapper.toResponse(author)).thenReturn(response);

        // When
        Page<AuthorResponseTo> result = authorService.getAll(pageable);

        // Then
        assertNotNull(result);
        assertEquals(1, result.getTotalElements());
        verify(authorRepository).findAll(pageable);
    }

    @Test
    void update_ShouldReturnUpdatedAuthor() {
        // Given
        Author existingAuthor = new Author("old@example.com", "oldpass", "Old", "User");
        existingAuthor.setId(1L);

        when(authorRepository.findById(1L)).thenReturn(Optional.of(existingAuthor));
        when(authorRepository.save(any(Author.class))).thenReturn(existingAuthor);
        when(authorMapper.toResponse(existingAuthor)).thenReturn(response);

        // When
        AuthorResponseTo result = authorService.update(1L, request);

        // Then
        assertNotNull(result);
        assertEquals(1L, result.getId());
        verify(authorRepository).save(existingAuthor);
    }

    @Test
    void delete_ShouldDeleteAuthor_WhenExists() {
        // Given
        when(authorRepository.existsById(1L)).thenReturn(true);

        // When
        authorService.delete(1L);

        // Then
        verify(authorRepository).deleteById(1L);
    }

    @Test
    void delete_ShouldThrowException_WhenNotExists() {
        // Given
        when(authorRepository.existsById(1L)).thenReturn(false);

        // When & Then
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
            () -> authorService.delete(1L));
        assertEquals("Author not found with id: 1", exception.getMessage());
    }
}