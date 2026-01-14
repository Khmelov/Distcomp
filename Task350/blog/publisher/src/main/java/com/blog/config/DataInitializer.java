package com.blog.config;

import com.blog.model.Editor;
import com.blog.repository.EditorRepository;
import com.blog.repository.TagRepository;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

@Component
/*@Profile("!test") */ // Отключаем при тестах
public class DataInitializer {

    @Autowired
    private EditorRepository editorRepository;
    @Autowired
    private TagRepository tagRepository;

    @PostConstruct
    public void init() {

        editorRepository.deleteAll();
        tagRepository.deleteAll();


        // Просто выводим сообщение
        System.out.println("DataInitializer запущен, но инициализация отключена");
    }


}