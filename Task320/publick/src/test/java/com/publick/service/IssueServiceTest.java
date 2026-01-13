package com.publick.service;

import com.publick.dto.IssueRequestTo;
import com.publick.dto.IssueResponseTo;
import com.publick.entity.Author;
import com.publick.entity.Issue;
import com.publick.repository.AuthorRepository;
import com.publick.repository.IssueRepository;
import com.publick.service.mapper.IssueMapper;
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

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class IssueServiceTest {

    @Mock
    private IssueRepository issueRepository;

    @Mock
    private AuthorRepository authorRepository;

    @Mock
    private IssueMapper issueMapper;

    @InjectMocks
    private IssueService issueService;

    private Author author;
    private Issue issue;
    private IssueRequestTo request;
    private IssueResponseTo response;

    @BeforeEach
    void setUp() {
        author = new Author("test@example.com", "password123", "John", "Doe");
        author.setId(1L);

        issue = new Issue(author, "Test Issue", "Test content");
        issue.setId(1L);
        issue.setCreated(LocalDateTime.now());
        issue.setModified(LocalDateTime.now());

        request = new IssueRequestTo();
        request.setAuthorId(1L);
        request.setTitle("Test Issue");
        request.setContent("Test content");

        response = new IssueResponseTo();
        response.setId(1L);
        response.setAuthorId(1L);
        response.setTitle("Test Issue");
        response.setContent("Test content");
        response.setCreated(issue.getCreated());
        response.setModified(issue.getModified());
    }

    @Test
    void create_ShouldReturnCreatedIssue() {
        // Given
        when(authorRepository.findById(1L)).thenReturn(Optional.of(author));
        when(issueMapper.toEntity(request, author)).thenReturn(issue);
        when(issueRepository.save(issue)).thenReturn(issue);
        when(issueMapper.toResponse(issue)).thenReturn(response);

        // When
        IssueResponseTo result = issueService.create(request);

        // Then
        assertNotNull(result);
        assertEquals(1L, result.getId());
        assertEquals("Test Issue", result.getTitle());
        verify(issueRepository).save(issue);
        verify(issueMapper).toResponse(issue);
    }

    @Test
    void create_ShouldThrowException_WhenAuthorNotFound() {
        // Given
        when(authorRepository.findById(1L)).thenReturn(Optional.empty());

        // When & Then
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
            () -> issueService.create(request));
        assertTrue(exception.getMessage().contains("Author not found"));
    }

    @Test
    void getById_ShouldReturnIssue_WhenExists() {
        // Given
        when(issueRepository.findById(1L)).thenReturn(Optional.of(issue));
        when(issueMapper.toResponse(issue)).thenReturn(response);

        // When
        IssueResponseTo result = issueService.getById(1L);

        // Then
        assertNotNull(result);
        assertEquals(1L, result.getId());
        verify(issueRepository).findById(1L);
    }

    @Test
    void getById_ShouldThrowException_WhenNotExists() {
        // Given
        when(issueRepository.findById(1L)).thenReturn(Optional.empty());

        // When & Then
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
            () -> issueService.getById(1L));
        assertTrue(exception.getMessage().contains("Issue not found"));
    }

    @Test
    void getAll_ShouldReturnAllIssues() {
        // Given
        List<Issue> issues = Arrays.asList(issue);
        when(issueRepository.findAll()).thenReturn(issues);
        when(issueMapper.toResponse(issue)).thenReturn(response);

        // When
        List<IssueResponseTo> result = issueService.getAll();

        // Then
        assertNotNull(result);
        assertEquals(1, result.size());
        verify(issueRepository).findAll();
    }

    @Test
    void getAllPaged_ShouldReturnPagedIssues() {
        // Given
        Pageable pageable = PageRequest.of(0, 10);
        List<Issue> issues = Arrays.asList(issue);
        Page<Issue> issuePage = new PageImpl<>(issues, pageable, 1);
        Page<IssueResponseTo> responsePage = new PageImpl<>(Arrays.asList(response), pageable, 1);

        when(issueRepository.findAll(pageable)).thenReturn(issuePage);
        when(issueMapper.toResponse(issue)).thenReturn(response);

        // When
        Page<IssueResponseTo> result = issueService.getAll(pageable);

        // Then
        assertNotNull(result);
        assertEquals(1, result.getTotalElements());
        verify(issueRepository).findAll(pageable);
    }

    @Test
    void update_ShouldReturnUpdatedIssue() {
        // Given
        Issue existingIssue = new Issue(author, "Old Issue", "Old content");
        existingIssue.setId(1L);

        when(issueRepository.findById(1L)).thenReturn(Optional.of(existingIssue));
        when(authorRepository.findById(1L)).thenReturn(Optional.of(author));
        when(issueRepository.save(any(Issue.class))).thenReturn(existingIssue);
        when(issueMapper.toResponse(existingIssue)).thenReturn(response);

        // When
        IssueResponseTo result = issueService.update(1L, request);

        // Then
        assertNotNull(result);
        assertEquals(1L, result.getId());
        verify(issueRepository).save(existingIssue);
    }

    @Test
    void delete_ShouldDeleteIssue_WhenExists() {
        // Given
        when(issueRepository.existsById(1L)).thenReturn(true);

        // When
        issueService.delete(1L);

        // Then
        verify(issueRepository).deleteById(1L);
    }

    @Test
    void delete_ShouldThrowException_WhenNotExists() {
        // Given
        when(issueRepository.existsById(1L)).thenReturn(false);

        // When & Then
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
            () -> issueService.delete(1L));
        assertTrue(exception.getMessage().contains("Issue not found"));
    }
}