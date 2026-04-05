package by.bsuir.distcomp.discussion.service;

import by.bsuir.distcomp.discussion.cassandra.ReactionCassandraRepository;
import by.bsuir.distcomp.discussion.cassandra.ReactionRow;
import by.bsuir.distcomp.discussion.client.PublisherTweetClient;
import by.bsuir.distcomp.discussion.dto.request.ReactionRequestTo;
import by.bsuir.distcomp.discussion.dto.response.ReactionResponseTo;
import by.bsuir.distcomp.discussion.entity.Reaction;
import by.bsuir.distcomp.discussion.exception.ResourceNotFoundException;
import by.bsuir.distcomp.discussion.mapper.ReactionMapper;
import by.bsuir.distcomp.discussion.model.ReactionState;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.concurrent.ThreadLocalRandom;
import java.util.stream.Collectors;

@Service
public class ReactionService {

    private final ReactionCassandraRepository reactionRepository;
    private final PublisherTweetClient publisherTweetClient;
    private final ReactionMapper reactionMapper;
    private final ModerationService moderationService;

    public ReactionService(ReactionCassandraRepository reactionRepository,
                           PublisherTweetClient publisherTweetClient,
                           ReactionMapper reactionMapper,
                           ModerationService moderationService) {
        this.reactionRepository = reactionRepository;
        this.publisherTweetClient = publisherTweetClient;
        this.reactionMapper = reactionMapper;
        this.moderationService = moderationService;
    }

    public ReactionResponseTo create(ReactionRequestTo dto) {
        publisherTweetClient.requireTweetExists(dto.getTweetId());
        Reaction entity = reactionMapper.toEntity(dto);
        entity.setId(nextId());
        entity.setState(moderationService.moderate(dto.getContent()));
        ReactionRow saved = reactionRepository.save(reactionMapper.toRow(entity));
        return reactionMapper.toResponseDto(reactionMapper.fromRow(saved));
    }

    public ReactionResponseTo getById(Long id) {
        Reaction entity = reactionRepository.findById(id)
                .map(reactionMapper::fromRow)
                .orElseThrow(() -> new ResourceNotFoundException("Reaction with id " + id + " not found", 40413));
        return reactionMapper.toResponseDto(entity);
    }

    public List<ReactionResponseTo> getAll() {
        return reactionRepository.findAll().stream()
                .map(reactionMapper::fromRow)
                .map(reactionMapper::toResponseDto)
                .collect(Collectors.toList());
    }

    public ReactionResponseTo update(ReactionRequestTo dto) {
        ReactionRow existingRow = reactionRepository.findById(dto.getId())
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Reaction with id " + dto.getId() + " not found", 40414));
        publisherTweetClient.requireTweetExists(dto.getTweetId());
        Reaction existing = reactionMapper.fromRow(existingRow);
        reactionMapper.updateEntityFromDto(dto, existing);
        existing.setState(moderationService.moderate(dto.getContent()));
        ReactionRow updated = reactionRepository.save(reactionMapper.toRow(existing));
        return reactionMapper.toResponseDto(reactionMapper.fromRow(updated));
    }

    public void deleteById(Long id) {
        if (!reactionRepository.existsById(id)) {
            throw new ResourceNotFoundException("Reaction with id " + id + " not found", 40416);
        }
        reactionRepository.deleteById(id);
    }

    private static long nextId() {
        return System.currentTimeMillis() * 10_000L + ThreadLocalRandom.current().nextInt(10_000);
    }
}
