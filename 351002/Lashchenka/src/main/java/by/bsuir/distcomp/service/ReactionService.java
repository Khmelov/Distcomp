package by.bsuir.distcomp.service;

import by.bsuir.distcomp.dto.request.ReactionRequestTo;
import by.bsuir.distcomp.dto.response.ReactionResponseTo;
import by.bsuir.distcomp.entity.Reaction;
import by.bsuir.distcomp.exception.ResourceNotFoundException;
import by.bsuir.distcomp.mapper.ReactionMapper;
import by.bsuir.distcomp.repository.impl.InMemoryReactionRepository;
import by.bsuir.distcomp.repository.impl.InMemoryTweetRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class ReactionService {

    private final InMemoryReactionRepository reactionRepository;
    private final InMemoryTweetRepository tweetRepository;
    private final ReactionMapper reactionMapper;

    public ReactionService(InMemoryReactionRepository reactionRepository,
                           InMemoryTweetRepository tweetRepository,
                           ReactionMapper reactionMapper) {
        this.reactionRepository = reactionRepository;
        this.tweetRepository = tweetRepository;
        this.reactionMapper = reactionMapper;
    }

    public ReactionResponseTo create(ReactionRequestTo dto) {
        if (!tweetRepository.existsById(dto.getTweetId())) {
            throw new ResourceNotFoundException("Tweet with id " + dto.getTweetId() + " not found", 40412);
        }
        Reaction entity = reactionMapper.toEntity(dto);
        Reaction saved = reactionRepository.save(entity);
        return reactionMapper.toResponseDto(saved);
    }

    public ReactionResponseTo getById(Long id) {
        Reaction entity = reactionRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Reaction with id " + id + " not found", 40413));
        return reactionMapper.toResponseDto(entity);
    }

    public List<ReactionResponseTo> getAll() {
        return reactionRepository.findAll().stream()
                .map(reactionMapper::toResponseDto)
                .collect(Collectors.toList());
    }

    public ReactionResponseTo update(ReactionRequestTo dto) {
        if (!reactionRepository.existsById(dto.getId())) {
            throw new ResourceNotFoundException("Reaction with id " + dto.getId() + " not found", 40414);
        }
        if (!tweetRepository.existsById(dto.getTweetId())) {
            throw new ResourceNotFoundException("Tweet with id " + dto.getTweetId() + " not found", 40415);
        }
        Reaction entity = reactionMapper.toEntity(dto);
        Reaction updated = reactionRepository.update(entity);
        return reactionMapper.toResponseDto(updated);
    }

    public void deleteById(Long id) {
        if (!reactionRepository.existsById(id)) {
            throw new ResourceNotFoundException("Reaction with id " + id + " not found", 40416);
        }
        reactionRepository.deleteById(id);
    }
}
