package com.blog.discussion.config;

import org.springframework.stereotype.Component;

import java.util.concurrent.atomic.AtomicLong;

@Component
public class IdGenerator {
    private final AtomicLong counter = new AtomicLong(1);

    public Long getNextId() {
        return counter.getAndIncrement();
    }
}