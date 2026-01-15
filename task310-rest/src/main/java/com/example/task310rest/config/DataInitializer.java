package com.example.task310rest.config;

import com.example.task310rest.dto.request.UserRequestTo;
import com.example.task310rest.service.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

/**
 * Инициализатор данных
 * Создает первого пользователя при запуске приложения
 */
@Component
@RequiredArgsConstructor
@Slf4j
public class DataInitializer implements CommandLineRunner {
    
    private final UserService userService;
    
    @Override
    public void run(String... args) throws Exception {
        log.info("Initializing default data...");
        
        // Создаем первого пользователя согласно требованиям
        UserRequestTo firstUser = UserRequestTo.builder()
                .login("nikita.malakhov022@gmail.com")
                .password("password123")
                .firstname("Никита")
                .lastname("Малахов")
                .build();
        
        try {
            userService.create(firstUser);
            log.info("Default user created successfully: {}", firstUser.getLogin());
        } catch (Exception e) {
            log.warn("Failed to create default user (may already exist): {}", e.getMessage());
        }
    }
}
