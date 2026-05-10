package com.adashkevich.kafka.lab.discussion.service;

import com.adashkevich.kafka.lab.discussion.dto.MessageRequestTo;
import com.adashkevich.kafka.lab.discussion.dto.MessageResponseTo;
import com.adashkevich.kafka.lab.discussion.exception.NotFoundException;
import com.adashkevich.kafka.lab.discussion.repository.MessageRepository;
import com.adashkevich.kafka.lab.discussion.model.Message;
import com.adashkevich.kafka.lab.discussion.model.MessageKey;
import com.adashkevich.kafka.lab.discussion.model.MessageState;
import org.springframework.stereotype.Service;

import java.util.Comparator;
import java.util.List;
import java.util.Set;
import java.util.concurrent.atomic.AtomicLong;

@Service
public class MessageService {
    private static final String PARTITION_COUNTRY = "BY";
    private static final Set<String> STOP_WORDS = Set.of("spam", "scam", "fraud", "hate");

    private final MessageRepository repo;
    private final AtomicLong idSequence = new AtomicLong(System.currentTimeMillis());

    public MessageService(MessageRepository repo) {
        this.repo = repo;
    }

    public MessageResponseTo create(MessageRequestTo dto) {
        return createModerated(idSequence.incrementAndGet(), dto.newsId, dto.content);
    }

    public MessageResponseTo createModerated(Long id, Long newsId, String content) {
        Message message = new Message();
        message.setKey(new MessageKey(PARTITION_COUNTRY, newsId, id));
        message.setContent(content);
        message.setState(moderate(content).name());
        return toResponse(repo.save(message));
    }

    public List<MessageResponseTo> getAll() {
        return repo.findAll().stream()
                .sorted(Comparator.comparing(m -> m.getKey().getId()))
                .map(this::toResponse)
                .toList();
    }

    public MessageResponseTo getById(Long id) {
        return toResponse(findExisting(id));
    }

    public MessageResponseTo update(Long id, MessageRequestTo dto) {
        Message existing = findExisting(id);

        repo.delete(existing);

        Message updated = new Message();
        updated.setKey(new MessageKey(PARTITION_COUNTRY, dto.newsId, id));
        updated.setContent(dto.content);
        updated.setState(moderate(dto.content).name());

        return toResponse(repo.save(updated));
    }

    public void delete(Long id) {
        repo.delete(findExisting(id));
    }

    public List<MessageResponseTo> getByNewsId(Long newsId) {
        return repo.findAll().stream()
                .filter(message -> newsId.equals(message.getKey().getNewsId()))
                .sorted(Comparator.comparing(m -> m.getKey().getId()))
                .map(this::toResponse)
                .toList();
    }

    public void deleteByNewsId(Long newsId) {
        List<Message> messages = repo.findAll().stream()
                .filter(message -> newsId.equals(message.getKey().getNewsId()))
                .toList();

        if (!messages.isEmpty()) {
            repo.deleteAll(messages);
        }
    }

    private Message findExisting(Long id) {
        return repo.findAll().stream()
                .filter(message -> id.equals(message.getKey().getId()))
                .findFirst()
                .orElseThrow(() -> new NotFoundException("Message not found", "40440"));
    }

    private MessageState moderate(String content) {
        String normalized = content == null ? "" : content.toLowerCase();
        return STOP_WORDS.stream().anyMatch(normalized::contains)
                ? MessageState.DECLINE
                : MessageState.APPROVE;
    }

    private MessageResponseTo toResponse(Message message) {
        MessageResponseTo dto = new MessageResponseTo();
        dto.id = message.getKey().getId();
        dto.newsId = message.getKey().getNewsId();
        dto.content = message.getContent();
        dto.state = message.getState();
        return dto;
    }
}