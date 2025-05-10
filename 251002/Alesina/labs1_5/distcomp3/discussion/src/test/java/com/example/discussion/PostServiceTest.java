package com.example.discussion;

import com.example.discussion.dto.PostRequestTo;
import com.example.discussion.dto.PostResponseTo;
import com.example.discussion.model.Post;
import com.example.discussion.repository.PostRepository;
import com.example.discussion.service.PostService;
import com.example.discussion.service.SequenceGeneratorService;
import com.example.discussion.service.mapper.PostMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mapstruct.factory.Mappers;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class PostServiceTest {

    private PostRepository postRepository;
    private SequenceGeneratorService sequenceGeneratorService;
    private PostMapper postMapper;
    private PostService postService;

    @BeforeEach
    void setUp() {
        postRepository = mock(PostRepository.class);
        sequenceGeneratorService = mock(SequenceGeneratorService.class);
        postMapper = Mappers.getMapper(PostMapper.class);

        postService = new PostService(postRepository, sequenceGeneratorService);
    }

    @Test
    void findAll_ShouldReturnNoteResponseToList() {
        Post post = new Post("Test content", 1L);
        when(postRepository.findAll()).thenReturn(Collections.singletonList(post));

        List<PostResponseTo> result = postService.findAll();

        assertEquals(1, result.size());
        assertEquals("Test content", result.get(0).getContent());
    }

    @Test
    void findById_WhenNoteExists_ShouldReturnNoteResponseTo() {
        Post post = new Post("Test content", 1L);
        when(postRepository.findById(1L)).thenReturn(Optional.of(post));

        PostResponseTo result = postService.findById(1L);

        assertNotNull(result);
        assertEquals(1L, result.getPostId());
        assertEquals("Test content", result.getContent());
    }

    @Test
    void findById_WhenNoteNotFound_ShouldReturnNull() {
        when(postRepository.findById(1L)).thenReturn(Optional.empty());

        PostResponseTo result = postService.findById(1L);

        assertNull(result);
    }

    @Test
    void save_ShouldReturnSavedNoteResponseTo() {
        PostRequestTo request = new PostRequestTo();
        request.setContent("New Note");
        request.setTopicId(1L);

        Post post = postMapper.toEntity(request);
        post.setId(100L);

        when(sequenceGeneratorService.generateSequence("note_sequence")).thenReturn(100L);
        when(postRepository.save(any(Post.class))).thenAnswer(invocation -> invocation.getArgument(0));

        PostResponseTo result = postService.save(request);

        assertNotNull(result);
        assertEquals("New Note", result.getContent());
        assertEquals(1L, result.getPostId());
        assertEquals(100L, result.getId());
    }

    @Test
    void update_WhenNoteExists_ShouldReturnUpdatedNoteResponseTo() {
        Post existingPost = new Post("Old content", 1L);
        PostRequestTo request = new PostRequestTo();
        request.setId(1L);
        request.setContent("Updated content");
        request.setTopicId(1L);

        when(postRepository.findById(1L)).thenReturn(Optional.of(existingPost));
        when(postRepository.save(any(Post.class))).thenAnswer(invocation -> invocation.getArgument(0));

        PostResponseTo result = postService.update(request);

        assertEquals("Updated content", result.getContent());
    }

    @Test
    void update_WhenNoteNotFound_ShouldThrowException() {
        PostRequestTo request = new PostRequestTo();
        request.setId(99L);
        request.setContent("Doesn't matter");
        request.setTopicId(1L);

        when(postRepository.findById(99L)).thenReturn(Optional.empty());

        RuntimeException ex = assertThrows(RuntimeException.class, () -> postService.update(request));
        assertEquals("Note not found", ex.getMessage());
    }

    @Test
    void deleteById_ShouldCallRepository() {
        doNothing().when(postRepository).deleteById(1L);

        postService.deleteById(1L);

        verify(postRepository).deleteById(1L);
    }
}
