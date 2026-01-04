package com.blog.service;

import com.blog.repository.EditorRepository;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class TestService {

    @Autowired
    private EditorRepository editorRepository;

    @PostConstruct
    public void cleanupTestData() {
        try {
            // Удаляем всех пользователей с логинами, которые могут быть созданы тестами
            editorRepository.findAll().forEach(editor -> {
                String login = editor.getLogin();
                if (login != null &&
                        (login.startsWith("editor") ||
                                login.startsWith("customer") ||
                                login.matches(".*\\d{3,}"))) { // логины с цифрами
                    editorRepository.delete(editor);
                    System.out.println("Deleted test user: " + login);
                }
            });
            System.out.println("Test data cleanup completed");
        } catch (Exception e) {
            System.err.println("Error during test data cleanup: " + e.getMessage());
        }
    }
}