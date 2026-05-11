package com.adashkevich.nosql.lab;

import com.adashkevich.nosql.lab.model.Editor;
import com.adashkevich.nosql.lab.repository.EditorRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

@Configuration
@Profile("!test")
public class DataInitializer {
    @Bean
    CommandLineRunner initDefaultEditor(EditorRepository editorRepository) {
        return args -> {
            if (editorRepository.count() == 0) {
                Editor editor = new Editor();
                editor.setLogin("antninadashkev@gmail.com");
                editor.setPassword("password123");
                editor.setFirstname("Antonina");
                editor.setLastname("Dashkevich");
                editorRepository.save(editor);
            }
        };
    }
}
