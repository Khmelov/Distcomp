package by.bsuir.distcomp.discussion.service;

import org.springframework.stereotype.Component;

import java.util.concurrent.atomic.AtomicLong;

@Component
public class ReactionIdGenerator {

    private final AtomicLong counter = new AtomicLong(System.currentTimeMillis() % 1_000_000_000L * 1000);

    public long nextId() {
        return counter.incrementAndGet();
    }
}
