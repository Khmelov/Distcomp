package com.blog.service;

import com.blog.dto.request.TagRequestTo;
import com.blog.dto.response.TagResponseTo;
import com.blog.exception.ResourceNotFoundException;
import com.blog.mapper.TagMapper;
import com.blog.model.Tag;
import com.blog.repository.TagRepository;
import com.blog.service.impl.TagServiceImpl;
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
class TagServiceUnitTest {

    @Mock
    private TagRepository tagRepository;

    @Mock
    private TagMapper tagMapper;

    @InjectMocks
    private TagServiceImpl tagService;

    private Tag tag;
    private TagRequestTo tagRequest;
    private TagResponseTo tagResponse;

    @BeforeEach
    void setUp() {
        tag = new Tag();
        tag.setId(1L);
        tag.setName("Java");

        tagRequest = new TagRequestTo();
        tagRequest.setName("Java");

        tagResponse = new TagResponseTo();
        tagResponse.setId(1L);
        tagResponse.setName("Java");
    }

    @Test
    void shouldGetAllTags() {
        // Given
        List<Tag> tags = Arrays.asList(tag);
        when(tagRepository.findAll()).thenReturn(tags);
        when(tagMapper.toResponse(tag)).thenReturn(tagResponse);

        // When
        List<TagResponseTo> result = tagService.getAll();

        // Then
        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals("Java", result.get(0).getName());
        verify(tagRepository, times(1)).findAll();
    }

    @Test
    void shouldGetAllTagsWithPagination() {
        // Given
        Pageable pageable = PageRequest.of(0, 10);
        Page<Tag> tagPage = new PageImpl<>(Arrays.asList(tag));
        when(tagRepository.findAll(pageable)).thenReturn(tagPage);
        when(tagMapper.toResponse(tag)).thenReturn(tagResponse);

        // When
        Page<TagResponseTo> result = tagService.getAll(pageable);

        // Then
        assertNotNull(result);
        assertEquals(1, result.getTotalElements());
        verify(tagRepository, times(1)).findAll(pageable);
    }

    @Test
    void shouldGetTagById() {
        // Given
        when(tagRepository.findById(1L)).thenReturn(Optional.of(tag));
        when(tagMapper.toResponse(tag)).thenReturn(tagResponse);

        // When
        TagResponseTo result = tagService.getById(1L);

        // Then
        assertNotNull(result);
        assertEquals(1L, result.getId());
        assertEquals("Java", result.getName());
        verify(tagRepository, times(1)).findById(1L);
    }

    @Test
    void shouldThrowExceptionWhenTagNotFound() {
        // Given
        when(tagRepository.findById(999L)).thenReturn(Optional.empty());

        // When & Then
        assertThrows(ResourceNotFoundException.class, () -> tagService.getById(999L));
        verify(tagRepository, times(1)).findById(999L);
    }

    @Test
    void shouldCreateTag() {
        // Given
        when(tagRepository.existsByName("Java")).thenReturn(false);
        when(tagMapper.toEntity(tagRequest)).thenReturn(tag);
        when(tagRepository.save(any(Tag.class))).thenReturn(tag);
        when(tagMapper.toResponse(tag)).thenReturn(tagResponse);

        // When
        TagResponseTo result = tagService.create(tagRequest);

        // Then
        assertNotNull(result);
        assertEquals("Java", result.getName());
        verify(tagRepository, times(1)).existsByName("Java");
        verify(tagRepository, times(1)).save(any(Tag.class));
    }

    @Test
    void shouldThrowExceptionWhenCreatingTagWithDuplicateName() {
        // Given
        when(tagRepository.existsByName("Java")).thenReturn(true);

        // When & Then
        assertThrows(IllegalArgumentException.class, () -> tagService.create(tagRequest));
        verify(tagRepository, times(1)).existsByName("Java");
        verify(tagRepository, never()).save(any(Tag.class));
    }

    @Test
    void shouldUpdateTag() {
        // Given
        when(tagRepository.findById(1L)).thenReturn(Optional.of(tag));
        when(tagRepository.existsByName("Spring")).thenReturn(false);
        when(tagRepository.save(any(Tag.class))).thenReturn(tag);
        when(tagMapper.toResponse(tag)).thenReturn(tagResponse);

        tagRequest.setName("Spring");

        // When
        TagResponseTo result = tagService.update(1L, tagRequest);

        // Then
        assertNotNull(result);
        verify(tagRepository, times(1)).findById(1L);
        verify(tagRepository, times(1)).existsByName("Spring");
        verify(tagRepository, times(1)).save(any(Tag.class));
    }

    @Test
    void shouldDeleteTag() {
        // Given
        when(tagRepository.findById(1L)).thenReturn(Optional.of(tag));

        // When
        tagService.delete(1L);

        // Then
        verify(tagRepository, times(1)).findById(1L);
        verify(tagRepository, times(1)).delete(tag);
    }

    @Test
    void shouldThrowExceptionWhenDeletingTagUsedInTopics() {
        // Given
        tag.getTopics().add(new com.blog.model.Topic());
        when(tagRepository.findById(1L)).thenReturn(Optional.of(tag));

        // When & Then
        assertThrows(IllegalStateException.class, () -> tagService.delete(1L));
        verify(tagRepository, times(1)).findById(1L);
        verify(tagRepository, never()).delete(any(Tag.class));
    }

    @Test
    void shouldFindTagByName() {
        // Given
        when(tagRepository.findByName("Java")).thenReturn(Optional.of(tag));
        when(tagMapper.toResponse(tag)).thenReturn(tagResponse);

        // When
        TagResponseTo result = tagService.findByName("Java");

        // Then
        assertNotNull(result);
        assertEquals("Java", result.getName());
        verify(tagRepository, times(1)).findByName("Java");
    }
}