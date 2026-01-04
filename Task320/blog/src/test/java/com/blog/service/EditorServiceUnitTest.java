package com.blog.service;

import com.blog.dto.request.EditorRequestTo;
import com.blog.dto.response.EditorResponseTo;
import com.blog.exception.ResourceNotFoundException;
import com.blog.mapper.EditorMapper;
import com.blog.model.Editor;
import com.blog.repository.EditorRepository;
import com.blog.service.impl.EditorServiceImpl;
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
class EditorServiceUnitTest {

    @Mock
    private EditorRepository editorRepository;

    @Mock
    private EditorMapper editorMapper;

    @InjectMocks
    private EditorServiceImpl editorService;

    private Editor editor;
    private EditorRequestTo editorRequest;
    private EditorResponseTo editorResponse;

    @BeforeEach
    void setUp() {
        editor = new Editor();
        editor.setId(1L);
        editor.setLogin("test@example.com");
        editor.setPassword("password123");
        editor.setFirstname("Тестовый");
        editor.setLastname("Редактор");

        editorRequest = new EditorRequestTo();
        editorRequest.setLogin("test@example.com");
        editorRequest.setPassword("password123");
        editorRequest.setFirstname("Тестовый");
        editorRequest.setLastname("Редактор");

        editorResponse = new EditorResponseTo();
        editorResponse.setId(1L);
        editorResponse.setLogin("test@example.com");
        editorResponse.setFirstname("Тестовый");
        editorResponse.setLastname("Редактор");
    }

    @Test
    void shouldGetAllEditors() {
        // Given
        List<Editor> editors = Arrays.asList(editor);
        when(editorRepository.findAll()).thenReturn(editors);
        when(editorMapper.toResponse(editor)).thenReturn(editorResponse);

        // When
        List<EditorResponseTo> result = editorService.getAll();

        // Then
        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals("test@example.com", result.get(0).getLogin());
        verify(editorRepository, times(1)).findAll();
    }

    @Test
    void shouldGetAllEditorsWithPagination() {
        // Given
        Pageable pageable = PageRequest.of(0, 10);
        Page<Editor> editorPage = new PageImpl<>(Arrays.asList(editor));
        when(editorRepository.findAll(pageable)).thenReturn(editorPage);
        when(editorMapper.toResponse(editor)).thenReturn(editorResponse);

        // When
        Page<EditorResponseTo> result = editorService.getAll(pageable);

        // Then
        assertNotNull(result);
        assertEquals(1, result.getTotalElements());
        verify(editorRepository, times(1)).findAll(pageable);
    }

    @Test
    void shouldGetEditorById() {
        // Given
        when(editorRepository.findById(1L)).thenReturn(Optional.of(editor));
        when(editorMapper.toResponse(editor)).thenReturn(editorResponse);

        // When
        EditorResponseTo result = editorService.getById(1L);

        // Then
        assertNotNull(result);
        assertEquals(1L, result.getId());
        assertEquals("test@example.com", result.getLogin());
        verify(editorRepository, times(1)).findById(1L);
    }

    @Test
    void shouldThrowExceptionWhenEditorNotFound() {
        // Given
        when(editorRepository.findById(999L)).thenReturn(Optional.empty());

        // When & Then
        assertThrows(ResourceNotFoundException.class, () -> editorService.getById(999L));
        verify(editorRepository, times(1)).findById(999L);
    }

    @Test
    void shouldCreateEditor() {
        // Given
        when(editorRepository.existsByLogin("test@example.com")).thenReturn(false);
        when(editorMapper.toEntity(editorRequest)).thenReturn(editor);
        when(editorRepository.save(any(Editor.class))).thenReturn(editor);
        when(editorMapper.toResponse(editor)).thenReturn(editorResponse);

        // When
        EditorResponseTo result = editorService.create(editorRequest);

        // Then
        assertNotNull(result);
        assertEquals("test@example.com", result.getLogin());
        verify(editorRepository, times(1)).existsByLogin("test@example.com");
        verify(editorRepository, times(1)).save(any(Editor.class));
    }

    @Test
    void shouldThrowExceptionWhenCreatingEditorWithDuplicateLogin() {
        // Given
        when(editorRepository.existsByLogin("test@example.com")).thenReturn(true);

        // When & Then
        assertThrows(IllegalArgumentException.class, () -> editorService.create(editorRequest));
        verify(editorRepository, times(1)).existsByLogin("test@example.com");
        verify(editorRepository, never()).save(any(Editor.class));
    }

    @Test
    void shouldUpdateEditor() {
        // Given
        when(editorRepository.findById(1L)).thenReturn(Optional.of(editor));
        when(editorRepository.existsByLogin("updated@example.com")).thenReturn(false);
        when(editorRepository.save(any(Editor.class))).thenReturn(editor);
        when(editorMapper.toResponse(editor)).thenReturn(editorResponse);

        editorRequest.setLogin("updated@example.com");

        // When
        EditorResponseTo result = editorService.update(1L, editorRequest);

        // Then
        assertNotNull(result);
        verify(editorRepository, times(1)).findById(1L);
        verify(editorRepository, times(1)).existsByLogin("updated@example.com");
        verify(editorRepository, times(1)).save(any(Editor.class));
    }

    @Test
    void shouldDeleteEditor() {
        // Given
        when(editorRepository.existsById(1L)).thenReturn(true);

        // When
        editorService.delete(1L);

        // Then
        verify(editorRepository, times(1)).existsById(1L);
        verify(editorRepository, times(1)).deleteById(1L);
    }

    @Test
    void shouldThrowExceptionWhenDeletingNonExistentEditor() {
        // Given
        when(editorRepository.existsById(999L)).thenReturn(false);

        // When & Then
        assertThrows(ResourceNotFoundException.class, () -> editorService.delete(999L));
        verify(editorRepository, times(1)).existsById(999L);
        verify(editorRepository, never()).deleteById(anyLong());
    }
}