package com.task310.socialnetwork.repository.impl;

import com.task310.socialnetwork.model.Message;
import com.task310.socialnetwork.repository.MessageRepository;
import org.springframework.stereotype.Repository;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicLong;
import java.util.stream.Collectors;

@Repository
public class InMemoryMessageRepository implements MessageRepository {
    private final Map<Long, Message> messages = new HashMap<>();
    private final AtomicLong idCounter = new AtomicLong(1);

    @Override
    public List<Message> findAll() {
        return new ArrayList<>(messages.values());
    }

    @Override
    public Optional<Message> findById(Long id) {
        return Optional.ofNullable(messages.get(id));
    }

    @Override
    public Message save(Message message) {
        if (message.getId() == null) {
            message.setId(idCounter.getAndIncrement());
        }
        messages.put(message.getId(), message);
        return message;
    }

    @Override
    public Message update(Message message) {
        if (message.getId() == null || !messages.containsKey(message.getId())) {
            throw new IllegalArgumentException("Message not found");
        }
        messages.put(message.getId(), message);
        return message;
    }

    @Override
    public boolean deleteById(Long id) {
        return messages.remove(id) != null;
    }

    @Override
    public boolean existsById(Long id) {
        return messages.containsKey(id);
    }

    @Override
    public List<Message> findByTweetId(Long tweetId) {
        return messages.values().stream()
                .filter(message -> message.getTweetId().equals(tweetId))
                .collect(Collectors.toList());
    }
}