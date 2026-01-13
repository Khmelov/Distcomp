package com.publick.service;

import com.publick.dto.PostRequestTo;
import com.publick.dto.PostResponseTo;
import com.publick.entity.Author;
import com.publick.entity.Issue;
import com.publick.entity.Post;
import com.publick.repository.IssueRepository;
import com.publick.repository.PostRepository;
import com.publick.service.mapper.PostMapper;
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
class PostServiceTest {

    @Mock
    private PostRepository postRepository;

    @Mock
    private IssueRepository issueRepository;

    @Mock
    private PostMapper postMapper;

    @InjectMocks
    private PostService postService;

    private Author author;
    private Issue issue;
    private Post post;
    private PostRequestTo request;
    private PostResponseTo response;

    @BeforeEach
    void setUp() {
        author = new Author("test@example.com", "password123", "John", "Doe");
        author.setId(1L);

        issue = new Issue(author, "Test Issue", "Test content");
        issue.setId(1L);
        issue.setCreated(LocalDateTime.now());
        issue.setModified(LocalDateTime.now());

        post = new Post(issue, "Test post content");
        post.setId(1L);

        request = new PostRequestTo();
        request.setIssueId(1L);
        request.setContent("Test post content");

        response = new PostResponseTo();
        response.setId(1L);
        response.setIssueId(1L);
        response.setContent("Test post content");
    }

    @Test
    void create_ShouldReturnCreatedPost() {
        // Given
        when(issueRepository.findById(1L)).thenReturn(Optional.of(issue));
        when(postMapper.toEntity(request, issue)).thenReturn(post);
        when(postRepository.save(post)).thenReturn(post);
        when(postMapper.toResponse(post)).thenReturn(response);

        // When
        PostResponseTo result = postService.create(request);

        // Then
        assertNotNull(result);
        assertEquals(1L, result.getId());
        assertEquals("Test post content", result.getContent());
        verify(postRepository).save(post);
        verify(postMapper).toResponse(post);
    }

    @Test
    void create_ShouldThrowException_WhenIssueNotFound() {
        // Given
        when(issueRepository.findById(1L)).thenReturn(Optional.empty());

        // When & Then
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
            () -> postService.create(request));
        assertTrue(exception.getMessage().contains("Issue not found"));
    }

    @Test
    void getById_ShouldReturnPost_WhenExists() {
        // Given
        when(postRepository.findById(1L)).thenReturn(Optional.of(post));
        when(postMapper.toResponse(post)).thenReturn(response);

        // When
        PostResponseTo result = postService.getById(1L);

        // Then
        assertNotNull(result);
        assertEquals(1L, result.getId());
        verify(postRepository).findById(1L);
    }

    @Test
    void getById_ShouldThrowException_WhenNotExists() {
        // Given
        when(postRepository.findById(1L)).thenReturn(Optional.empty());

        // When & Then
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
            () -> postService.getById(1L));
        assertTrue(exception.getMessage().contains("Post not found"));
    }

    @Test
    void getAll_ShouldReturnAllPosts() {
        // Given
        List<Post> posts = Arrays.asList(post);
        when(postRepository.findAll()).thenReturn(posts);
        when(postMapper.toResponse(post)).thenReturn(response);

        // When
        List<PostResponseTo> result = postService.getAll();

        // Then
        assertNotNull(result);
        assertEquals(1, result.size());
        verify(postRepository).findAll();
    }

    @Test
    void getAllPaged_ShouldReturnPagedPosts() {
        // Given
        Pageable pageable = PageRequest.of(0, 10);
        List<Post> posts = Arrays.asList(post);
        Page<Post> postPage = new PageImpl<>(posts, pageable, 1);
        Page<PostResponseTo> responsePage = new PageImpl<>(Arrays.asList(response), pageable, 1);

        when(postRepository.findAll(pageable)).thenReturn(postPage);
        when(postMapper.toResponse(post)).thenReturn(response);

        // When
        Page<PostResponseTo> result = postService.getAll(pageable);

        // Then
        assertNotNull(result);
        assertEquals(1, result.getTotalElements());
        verify(postRepository).findAll(pageable);
    }

    @Test
    void update_ShouldReturnUpdatedPost() {
        // Given
        Post existingPost = new Post(issue, "Old content");
        existingPost.setId(1L);

        Issue newIssue = new Issue(author, "New Issue", "New content");
        newIssue.setId(2L);

        when(postRepository.findById(1L)).thenReturn(Optional.of(existingPost));
        when(issueRepository.findById(1L)).thenReturn(Optional.of(newIssue));
        when(postRepository.save(any(Post.class))).thenReturn(existingPost);
        when(postMapper.toResponse(existingPost)).thenReturn(response);

        // When
        PostResponseTo result = postService.update(1L, request);

        // Then
        assertNotNull(result);
        assertEquals(1L, result.getId());
        verify(postRepository).save(existingPost);
    }

    @Test
    void delete_ShouldDeletePost_WhenExists() {
        // Given
        when(postRepository.existsById(1L)).thenReturn(true);

        // When
        postService.delete(1L);

        // Then
        verify(postRepository).deleteById(1L);
    }

    @Test
    void delete_ShouldThrowException_WhenNotExists() {
        // Given
        when(postRepository.existsById(1L)).thenReturn(false);

        // When & Then
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
            () -> postService.delete(1L));
        assertTrue(exception.getMessage().contains("Post not found"));
    }
}