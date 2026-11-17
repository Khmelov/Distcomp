package by.bsuir.rest.support;

import by.bsuir.rest.client.DiscussionClient;
import by.bsuir.rest.dto.MessageState;
import by.bsuir.rest.dto.request.MessageRequestTo;
import by.bsuir.rest.dto.response.MessageResponseTo;
import by.bsuir.rest.exception.ApiException;

import java.util.Comparator;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.atomic.AtomicLong;

public class MemoryDiscussionClient implements DiscussionClient {

    private final AtomicLong ids = new AtomicLong();
    private final ConcurrentMap<Long, MessageResponseTo> rows = new ConcurrentHashMap<>();

    @Override
    public MessageResponseTo create(MessageRequestTo request) {
        long id = request.getId() != null ? request.getId() : ids.incrementAndGet();
        MessageResponseTo stored = copy(id, request.getStoryId(), request.getContent());
        rows.put(id, stored);
        return copy(stored);
    }

    @Override
    public MessageResponseTo getById(Long id) {
        MessageResponseTo stored = rows.get(id);
        if (stored == null) {
            throw ApiException.notFound("Message", id);
        }
        return copy(stored);
    }

    @Override
    public List<MessageResponseTo> getAll() {
        return rows.values().stream()
                .map(this::copy)
                .sorted(Comparator.comparing(MessageResponseTo::getId))
                .toList();
    }

    @Override
    public List<MessageResponseTo> findByStoryId(Long storyId) {
        return rows.values().stream()
                .filter(row -> storyId.equals(row.getStoryId()))
                .map(this::copy)
                .sorted(Comparator.comparing(MessageResponseTo::getId))
                .toList();
    }

    @Override
    public MessageResponseTo update(Long id, MessageRequestTo request) {
        if (!rows.containsKey(id)) {
            throw ApiException.notFound("Message", id);
        }
        MessageResponseTo stored = copy(id, request.getStoryId(), request.getContent());
        rows.put(id, stored);
        return copy(stored);
    }

    @Override
    public void delete(Long id) {
        if (rows.remove(id) == null) {
            throw ApiException.notFound("Message", id);
        }
    }

    private MessageResponseTo copy(MessageResponseTo source) {
        MessageResponseTo response = copy(source.getId(), source.getStoryId(), source.getContent());
        response.setState(source.getState());
        return response;
    }

    private MessageResponseTo copy(Long id, Long storyId, String content) {
        MessageResponseTo response = new MessageResponseTo();
        response.setId(id);
        response.setStoryId(storyId);
        response.setContent(content);
        response.setState(MessageState.APPROVE);
        return response;
    }
}
