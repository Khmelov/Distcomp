/*package com.blog.mapper;

import com.blog.dto.request.EditorRequestTo;
import com.blog.dto.response.EditorResponseTo;
import com.blog.model.Editor;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
class EditorMapperTest {

    @Autowired
    private EditorMapper editorMapper;

    @Test
    void shouldMapEditorRequestToEntity() {
        // Given
        EditorRequestTo request = new EditorRequestTo();
        request.setLogin("test@example.com");
        request.setPassword("password123");
        request.setFirstname("Test");
        request.setLastname("Editor");

        // When
        Editor entity = editorMapper.toEntity(request);

        // Then
        assertThat(entity).isNotNull();
        assertThat(entity.getLogin()).isEqualTo("test@example.com");
        assertThat(entity.getPassword()).isEqualTo("password123");
        assertThat(entity.getFirstname()).isEqualTo("Test");
        assertThat(entity.getLastname()).isEqualTo("Editor");
    }

    @Test
    void shouldMapEntityToEditorResponse() {
        // Given
        Editor entity = new Editor();
        entity.setId(1L);
        entity.setLogin("test@example.com");
        entity.setFirstname("Test");
        entity.setLastname("Editor");

        // When
        EditorResponseTo response = editorMapper.toResponse(entity);

        // Then
        assertThat(response).isNotNull();
        assertThat(response.getId()).isEqualTo(1L);
        assertThat(response.getLogin()).isEqualTo("test@example.com");
        assertThat(response.getFirstname()).isEqualTo("Test");
        assertThat(response.getLastname()).isEqualTo("Editor");
        assertThat(response).hasNoNullFieldsOrPropertiesExcept("password");
    }

    @Test
    void shouldReturnNullWhenRequestIsNull() {
        // When
        Editor entity = editorMapper.toEntity(null);

        // Then
        assertThat(entity).isNull();
    }

    @Test
    void shouldReturnNullWhenEntityIsNull() {
        // When
        EditorResponseTo response = editorMapper.toResponse(null);

        // Then
        assertThat(response).isNull();
    }
}*/