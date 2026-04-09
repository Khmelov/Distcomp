package com.example.task310.config;

import com.example.task310.service.MarkService;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

@Component
public class DataInitializer implements CommandLineRunner {

    private final MarkService markService;

    public DataInitializer(MarkService markService) {
        this.markService = markService;
    }

    @Override
    public void run(String... args) {
        // Ничего не делаем при старте
        // Метки будут создаваться по запросу
        System.out.println("✅ Система готова автоматически создавать метки");
    }
}