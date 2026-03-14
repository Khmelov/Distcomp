package com.example.Labs.config;

import com.example.Labs.entity.*;
import com.example.Labs.repository.InMemoryRepository;
import jakarta.annotation.PostConstruct;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class AppConfig {

    @Bean
    public InMemoryRepository<Editor> editorRepository() {
        return new InMemoryRepository<>(Editor::getId, Editor::setId);
    }

    @Bean
    public InMemoryRepository<Story> storyRepository() {
        return new InMemoryRepository<>(Story::getId, Story::setId);
    }

    @Bean
    public InMemoryRepository<Message> messageRepository() {
        return new InMemoryRepository<>(Message::getId, Message::setId);
    }

    @Bean
    public InMemoryRepository<Mark> markRepository() {
        return new InMemoryRepository<>(Mark::getId, Mark::setId);
    }

    // Инициализация ожидаемых данных из задания (winhelfer34@gmail.com, Даниил Гасюк)
    @Bean
    public DataInitializer dataInitializer(InMemoryRepository<Editor> editorRepository) {
        return new DataInitializer(editorRepository);
    }

    public static class DataInitializer {
        private final InMemoryRepository<Editor> editorRepository;

        public DataInitializer(InMemoryRepository<Editor> editorRepository) {
            this.editorRepository = editorRepository;
        }

        @PostConstruct
        public void init() {
            Editor defaultEditor = new Editor();
            defaultEditor.setLogin("winhelfer34@gmail.com");
            defaultEditor.setPassword("secretPassword");
            defaultEditor.setFirstname("Даниил");
            defaultEditor.setLastname("Гасюк");
            editorRepository.save(defaultEditor);
        }
    }
}