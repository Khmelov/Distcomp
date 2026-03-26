package com.lizaveta.discussion.service;

import org.springframework.stereotype.Component;

import java.util.concurrent.atomic.AtomicLong;

/**
 * Generates monotonic numeric ids for notices within a single JVM instance.
 */
@Component
public class NoticeIdGenerator {

    private final AtomicLong counter = new AtomicLong(System.currentTimeMillis());

    /**
     * @return next unique id for a new notice
     */
    public long nextId() {
        return counter.incrementAndGet();
    }
}
