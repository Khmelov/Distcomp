package by.bsuir.distcomp.publisher.service;

import org.springframework.stereotype.Component;
import java.util.concurrent.atomic.AtomicLong;

@Component
public class ReactionIdGenerator {
    private final AtomicLong counter = new AtomicLong(System.currentTimeMillis());
    public long nextId() { return counter.incrementAndGet(); }
}