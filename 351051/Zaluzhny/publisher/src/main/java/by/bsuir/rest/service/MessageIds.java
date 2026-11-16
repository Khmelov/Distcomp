package by.bsuir.rest.service;

import org.springframework.stereotype.Component;

import java.util.concurrent.atomic.AtomicLong;

@Component
public class MessageIds {

    private final AtomicLong next = new AtomicLong();

    public long nextId() {
        return next.incrementAndGet();
    }
}
