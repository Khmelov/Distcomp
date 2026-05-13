package com.example.discussion.service;

import com.example.discussion.dto.ReactionRequestTo;
import com.example.discussion.dto.ReactionResponseTo;
import com.example.discussion.entity.Reaction;
import com.example.discussion.entity.ReactionKey;
import com.example.discussion.repository.ReactionRepository;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.concurrent.atomic.AtomicLong;

@Service
public class ReactionService {

    private final ReactionRepository repository;
    private final AtomicLong idGenerator = new AtomicLong(1);

    public ReactionService(ReactionRepository repository) {
        this.repository = repository;
    }

    public ReactionResponseTo create(ReactionRequestTo dto) {
        Long newId = idGenerator.getAndIncrement();
        Reaction reaction = new Reaction();
        reaction.setTweetId(dto.getTweetId());
        reaction.setId(newId);
        reaction.setContent(dto.getContent());
        Reaction saved = repository.save(reaction);
        return toResponse(saved);
    }

    public List<ReactionResponseTo> getAll() {
        List<ReactionResponseTo> result = new ArrayList<>();
        repository.findAll().forEach(r -> result.add(toResponse(r)));
        return result;
    }

    public ReactionResponseTo get(Long id) {
        Reaction reaction = repository.findAll().stream()
                .filter(r -> r.getId() != null && r.getId().equals(id))
                .findFirst()
                .orElseThrow(() -> new NoSuchElementException("Reaction not found"));
        return toResponse(reaction);
    }

    public ReactionResponseTo update(Long id, ReactionRequestTo dto) {
        Reaction existing = repository.findAll().stream()
                .filter(r -> r.getId() != null && r.getId().equals(id))
                .findFirst()
                .orElseThrow(() -> new NoSuchElementException("Reaction not found"));
        existing.setContent(dto.getContent());
        existing.setTweetId(dto.getTweetId());
        repository.delete(existing);
        existing.setId(existing.getId()); // сохраняем тот же id
        Reaction saved = repository.save(existing);
        return toResponse(saved);
    }

    public void delete(Long id) {
        Reaction existing = repository.findAll().stream()
                .filter(r -> r.getId() != null && r.getId().equals(id))
                .findFirst()
                .orElseThrow(() -> new NoSuchElementException("Reaction not found"));
        repository.delete(existing);
    }

    private ReactionResponseTo toResponse(Reaction reaction) {
        return new ReactionResponseTo(
                reaction.getId(),
                reaction.getTweetId(),
                reaction.getContent()
        );
    }
}