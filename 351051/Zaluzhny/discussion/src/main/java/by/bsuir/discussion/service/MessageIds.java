package by.bsuir.discussion.service;

import by.bsuir.discussion.entity.MessageById;
import by.bsuir.discussion.repository.MessageByIdRepository;
import jakarta.annotation.PostConstruct;
import org.springframework.context.annotation.DependsOn;
import org.springframework.stereotype.Component;

import java.util.concurrent.atomic.AtomicLong;

@Component
@DependsOn("cassandraLiquibase")
public class MessageIds {

    private final MessageByIdRepository messages;
    private final AtomicLong sequence = new AtomicLong();

    public MessageIds(MessageByIdRepository messages) {
        this.messages = messages;
    }

    @PostConstruct
    public void loadExistingIds() {
        long max = 0L;
        for (MessageById row : messages.findAll()) {
            if (row.getId() != null && row.getId() > max) {
                max = row.getId();
            }
        }
        sequence.set(max);
    }

    public long nextId() {
        return sequence.incrementAndGet();
    }
}
