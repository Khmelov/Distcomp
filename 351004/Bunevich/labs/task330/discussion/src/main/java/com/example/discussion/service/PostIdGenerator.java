package com.example.discussion.service;

import org.springframework.stereotype.Component;

import java.time.Instant;
import java.util.concurrent.atomic.AtomicInteger;

@Component
public class PostIdGenerator {
    private final AtomicInteger counter = new AtomicInteger(0);

    public long nextId() {
        long millis = Instant.now().toEpochMilli();
        int suffix = counter.updateAndGet(value -> (value + 1) % 1000);
        return millis * 1000 + suffix;
    }
}
