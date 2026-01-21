package com.example.app.service;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import org.springframework.stereotype.Service;

import com.example.app.dto.ReactionRequestDTO;
import com.example.app.dto.ReactionResponseDTO;
import com.example.app.exception.AppException;
import com.example.app.model.Reaction;
import com.example.app.model.Tweet;
import com.example.app.repository.InMemoryReactionRepository;
import com.example.app.repository.InMemoryTweetRepository;

import java.util.List;

@Service
public class ReactionService {
    private final InMemoryReactionRepository reactionRepo;
    private final InMemoryTweetRepository tweetRepo;

    public ReactionService(InMemoryReactionRepository reactionRepo, InMemoryTweetRepository tweetRepo) {
        this.reactionRepo = reactionRepo;
        this.tweetRepo = tweetRepo;
    }

    public List<ReactionResponseDTO> getAllReactions() {
        return reactionRepo.findAll().stream()
                .map(this::toResponse)
                .toList();
    }

    public ReactionResponseDTO getReactionById(@NotNull Long id) {
        return reactionRepo.findById(id)
                .map(this::toResponse)
                .orElseThrow(() -> new AppException("Reaction not found", 40402));
    }

    public ReactionResponseDTO createReaction(@Valid ReactionRequestDTO request) {
        Tweet tweet = tweetRepo.findById(request.tweetId())
                .orElseThrow(() -> new AppException("Tweet not found for reaction creation", 40405));
        
        Reaction reaction = toEntity(request);
        Reaction saved = reactionRepo.save(reaction);
        return toResponse(saved);
    }

    public ReactionResponseDTO updateReaction(@Valid ReactionRequestDTO request) {
        if (request.id() == null) {
            throw new AppException("ID required for update", 40003);
        }
        if (!reactionRepo.findById(request.id()).isPresent()) {
            throw new AppException("Reaction not found for update", 40402);
        }
        
        tweetRepo.findById(request.tweetId())
                .orElseThrow(() -> new AppException("Tweet not found for reaction update", 40405));
        
        Reaction reaction = toEntity(request);
        Reaction updated = reactionRepo.save(reaction);
        return toResponse(updated);
    }

    public void deleteReaction(@NotNull Long id) {
        if (!reactionRepo.deleteById(id)) {
            throw new AppException("Reaction not found for deletion", 40402);
        }
    }

    private Reaction toEntity(ReactionRequestDTO dto) {
        Reaction reaction = new Reaction();
        reaction.setId(dto.id());
        reaction.setTweetId(dto.tweetId());
        reaction.setContent(dto.content());
        return reaction;
    }

    private ReactionResponseDTO toResponse(Reaction reaction) {
        return new ReactionResponseDTO(
                reaction.getId(),
                reaction.getTweetId(),
                reaction.getContent()
        );
    }
}