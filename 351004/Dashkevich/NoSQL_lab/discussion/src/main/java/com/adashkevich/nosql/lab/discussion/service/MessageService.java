package com.adashkevich.nosql.lab.discussion.service;

import com.adashkevich.nosql.lab.discussion.model.Message;
import com.adashkevich.nosql.lab.discussion.dto.MessageRequestTo;
import com.adashkevich.nosql.lab.discussion.dto.MessageResponseTo;
import com.adashkevich.nosql.lab.discussion.exception.NotFoundException;
import com.adashkevich.nosql.lab.discussion.model.MessageKey;
import com.adashkevich.nosql.lab.discussion.repository.MessageRepository;
import org.springframework.stereotype.Service;

import java.util.Comparator;
import java.util.List;
import java.util.concurrent.atomic.AtomicLong;

@Service
public class MessageService {
    private static final String PARTITION_COUNTRY = "BY";
    private final MessageRepository repo;
    private final AtomicLong idSequence = new AtomicLong(System.currentTimeMillis());

    public MessageService(MessageRepository repo) {
        this.repo = repo;
    }

    public MessageResponseTo create(MessageRequestTo dto) {
        Message message = new Message();
        message.setKey(new MessageKey(PARTITION_COUNTRY, dto.newsId, idSequence.incrementAndGet()));
        message.setContent(dto.content);
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
        return toResponse(repo.save(updated));
    }

    public void delete(Long id) {
        repo.delete(findExisting(id));
    }

    public List<MessageResponseTo> getByNewsId(Long newsId) {
        return repo.findByKeyNewsId(newsId).stream()
                .sorted(Comparator.comparing(m -> m.getKey().getId()))
                .map(this::toResponse)
                .toList();
    }

    public void deleteByNewsId(Long newsId) {
        repo.deleteAll(repo.findByKeyNewsId(newsId));
    }

    private Message findExisting(Long id) {
        return repo.findByKeyId(id).stream().findFirst()
                .orElseThrow(() -> new NotFoundException("Message not found", "40440"));
    }

    private MessageResponseTo toResponse(Message message) {
        MessageResponseTo dto = new MessageResponseTo();
        dto.id = message.getKey().getId();
        dto.newsId = message.getKey().getNewsId();
        dto.content = message.getContent();
        return dto;
    }
}
