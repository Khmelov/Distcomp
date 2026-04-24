package com.lizaveta.discussion.service;

import org.springframework.stereotype.Component;

import java.util.concurrent.atomic.AtomicLong;

@Component
public class NoticeIdGenerator {

    private final AtomicLong counter = new AtomicLong(System.currentTimeMillis());

    public long nextId() {
        return counter.incrementAndGet();
    }
}
