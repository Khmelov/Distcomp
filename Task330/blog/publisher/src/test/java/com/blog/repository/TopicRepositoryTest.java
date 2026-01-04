package com.blog.repository;

import com.blog.model.Editor;
import com.blog.model.Tag;
import com.blog.model.Topic;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.test.context.ActiveProfiles;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@ActiveProfiles("test")
class TopicRepositoryTest {

    @Autowired
    private TestEntityManager entityManager;

    @Autowired
    private TopicRepository topicRepository;

    @Test
    void shouldFindTopicsByEditorId() {
        // Given
        Editor editor = new Editor();
        editor.setLogin("editor@example.com");
        editor.setPassword("password123");
        editor.setFirstname("Test");
        editor.setLastname("Editor");
        entityManager.persist(editor);

        Topic topic = new Topic();
        topic.setEditor(editor);
        topic.setTitle("Test Topic");
        topic.setContent("Test content");
        entityManager.persist(topic);

        entityManager.flush();

        // When
        List<Topic> topics = topicRepository.findByEditorId(editor.getId());

        // Then
        assertThat(topics).hasSize(1);
        assertThat(topics.get(0).getTitle()).isEqualTo("Test Topic");
    }

    @Test
    void shouldReturnEmptyListWhenNoTopicsForEditor() {
        // When
        List<Topic> topics = topicRepository.findByEditorId(999L);

        // Then
        assertThat(topics).isEmpty();
    }
}