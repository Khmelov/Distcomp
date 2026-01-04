package com.blog.config;

import com.blog.model.Editor;
import com.blog.model.EditorRole;
import com.blog.repository.EditorRepository;
import com.blog.repository.TagRepository;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
public class DataInitializer {

    @Autowired
    private EditorRepository editorRepository;

    @Autowired
    private TagRepository tagRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @PostConstruct
    @Transactional
    public void init() {
        try {
            // Очищаем только тестовые данные
            cleanupTestData();

            // Создаем только базовых пользователей
            createBasicUsers();
            System.out.println("DataInitializer: Basic users created successfully");
        } catch (Exception e) {
            System.err.println("DataInitializer: Error creating basic users: " + e.getMessage());
        }
    }

    private void cleanupTestData() {
        // Удаляем только пользователей с тестовыми логинами
        editorRepository.findAll().forEach(editor -> {
            String login = editor.getLogin();
            if (login != null && login.matches("editor\\d+") || login.matches("customer\\d+")) {
                editorRepository.delete(editor);
            }
        });
    }

    private void createBasicUsers() {
        // Создаем администратора, если его еще нет
        if (!editorRepository.existsByLogin("admin")) {
            Editor admin = new Editor();
            admin.setLogin("admin");
            admin.setPassword(passwordEncoder.encode("admin123"));
            admin.setFirstname("Admin");
            admin.setLastname("User");
            admin.setRole(EditorRole.ADMIN);
            editorRepository.save(admin);
            System.out.println("Created admin user: admin/admin123");
        }

        // Создаем обычного пользователя, если его еще нет
        if (!editorRepository.existsByLogin("customer")) {
            Editor customer = new Editor();
            customer.setLogin("customer");
            customer.setPassword(passwordEncoder.encode("customer123"));
            customer.setFirstname("Customer");
            customer.setLastname("User");
            customer.setRole(EditorRole.CUSTOMER);
            editorRepository.save(customer);
            System.out.println("Created customer user: customer/customer123");
        }
    }
}