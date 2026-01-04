package com.blog.config;

import com.blog.model.Editor;
import com.blog.repository.EditorRepository;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class DataInitializer {

    @Autowired
    private EditorRepository editorRepository;

    @PostConstruct
    public void init() {
        // Проверяем, есть ли уже редакторы
        if (editorRepository.findAll().isEmpty()) {
            // Создаем первого редактора как указано в требованиях
            Editor editor = new Editor();
            editor.setLogin("andrey.bobrovskiy2003@gmail.com");
            editor.setPassword("password123");
            editor.setFirstname("Андрей");
            editor.setLastname("Бобровский");

            editorRepository.save(editor);
            System.out.println("Тестовый редактор создан: " + editor.getLogin());
            System.out.println("ID редактора: " + editor.getId());
        }
    }
}