package by.bsuir.distcomp.support;

import by.bsuir.distcomp.client.DiscussionReactionClient;
import by.bsuir.distcomp.core.exception.ResourceNotFoundException;
import by.bsuir.distcomp.dto.request.ReactionRequestTo;
import by.bsuir.distcomp.dto.reaction.ReactionState;
import by.bsuir.distcomp.dto.response.ReactionResponseTo;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;
import java.util.stream.Collectors;

/**
 * In-memory реализация для интеграционных тестов publisher без поднятого discussion.
 */
public class InMemoryDiscussionReactionClient implements DiscussionReactionClient {

    private final Map<Long, ReactionResponseTo> byId = new ConcurrentHashMap<>();
    private final AtomicLong ids = new AtomicLong(System.currentTimeMillis() % 1_000_000L * 10_000);

    @Override
    public ReactionResponseTo create(ReactionRequestTo dto) {
        long id = ids.incrementAndGet();
        ReactionResponseTo r = new ReactionResponseTo(id, dto.getTweetId(), dto.getContent(), ReactionState.APPROVE);
        byId.put(id, r);
        return r;
    }

    @Override
    public ReactionResponseTo getById(Long id) {
        ReactionResponseTo r = byId.get(id);
        if (r == null) {
            throw new ResourceNotFoundException("Reaction not found", 40417);
        }
        return r;
    }

    @Override
    public List<ReactionResponseTo> getAll() {
        return new ArrayList<>(byId.values());
    }

    @Override
    public List<ReactionResponseTo> getByTweetId(Long tweetId) {
        return byId.values().stream()
                .filter(x -> tweetId.equals(x.getTweetId()))
                .collect(Collectors.toList());
    }

    @Override
    public ReactionResponseTo update(ReactionRequestTo dto) {
        ReactionResponseTo existing = byId.get(dto.getId());
        if (existing == null) {
            throw new ResourceNotFoundException("Reaction not found", 40418);
        }
        ReactionResponseTo next = new ReactionResponseTo(dto.getId(), dto.getTweetId(), dto.getContent(), ReactionState.APPROVE);
        byId.put(dto.getId(), next);
        return next;
    }

    @Override
    public void deleteById(Long id) {
        if (!byId.containsKey(id)) {
            throw new ResourceNotFoundException("Reaction not found", 40420);
        }
        byId.remove(id);
    }

    @Override
    public void deleteByTweetId(Long tweetId) {
        byId.entrySet().removeIf(e -> tweetId.equals(e.getValue().getTweetId()));
    }
}
