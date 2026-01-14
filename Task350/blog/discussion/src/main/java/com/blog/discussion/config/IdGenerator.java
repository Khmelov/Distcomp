package com.blog.discussion.config;

import org.springframework.stereotype.Component;

import java.util.concurrent.atomic.AtomicLong;

@Component
public class IdGenerator {
    // Используем AtomicLong для локальной генерации
    private final AtomicLong counter = new AtomicLong(System.currentTimeMillis() % 1000000);

    public Long getNextId() {
        // Используем ID из запроса Publisher, если он есть
        // Этот генератор используется как резервный
        return counter.getAndIncrement();
    }

    // Метод для проверки, нужно ли генерировать ID
    public boolean shouldGenerateId(Long requestedId) {
        // Если ID предоставлен Publisher, используем его
        // Если нет - генерируем наш
        return requestedId == null;
    }
}