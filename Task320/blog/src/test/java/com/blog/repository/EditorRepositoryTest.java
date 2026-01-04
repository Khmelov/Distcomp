package com.blog.repository;

import com.blog.model.Editor;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.test.context.ActiveProfiles;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@ActiveProfiles("test")
class EditorRepositoryTest {

    @Autowired
    private TestEntityManager entityManager;

    @Autowired
    private EditorRepository editorRepository;

    @Test
    void shouldFindEditorByLogin() {
        // Given
        Editor editor = new Editor();
        editor.setLogin("test@example.com");
        editor.setPassword("password123");
        editor.setFirstname("Test");
        editor.setLastname("Editor");

        entityManager.persist(editor);
        entityManager.flush();

        // When
        Optional<Editor> found = editorRepository.findByLogin("test@example.com");

        // Then
        assertThat(found).isPresent();
        assertThat(found.get().getLogin()).isEqualTo("test@example.com");
    }

    @Test
    void shouldReturnEmptyWhenLoginNotFound() {
        // When
        Optional<Editor> found = editorRepository.findByLogin("nonexistent@example.com");

        // Then
        assertThat(found).isEmpty();
    }

    @Test
    void shouldCheckIfLoginExists() {
        // Given
        Editor editor = new Editor();
        editor.setLogin("existing@example.com");
        editor.setPassword("password123");
        editor.setFirstname("Existing");
        editor.setLastname("Editor");

        entityManager.persist(editor);
        entityManager.flush();

        // When
        boolean exists = editorRepository.existsByLogin("existing@example.com");

        // Then
        assertThat(exists).isTrue();
    }

    @Test
    void shouldReturnFalseWhenLoginDoesNotExist() {
        // When
        boolean exists = editorRepository.existsByLogin("nonexistent@example.com");

        // Then
        assertThat(exists).isFalse();
    }
}