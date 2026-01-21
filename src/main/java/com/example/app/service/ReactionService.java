package com.example.app.service;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.app.dto.ReactionRequestDTO;
import com.example.app.dto.ReactionResponseDTO;
import com.example.app.exception.AppException;
import com.example.app.model.Reaction;
import com.example.app.repository.ReactionRepository;
import com.example.app.repository.TweetRepository;

import java.util.List;

@Service
public class ReactionService {
    private final ReactionRepository reactionRepo;
    private final TweetRepository tweetRepo;

    public ReactionService(ReactionRepository reactionRepo, TweetRepository tweetRepo) {
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

    @Transactional
    public ReactionResponseDTO createReaction(@Valid ReactionRequestDTO request) {
        if (!tweetRepo.existsById(request.tweetId())) {
            throw new AppException("Tweet not found for reaction creation", 40405);
        }
        
        Reaction reaction = toEntity(request);
        Reaction saved = reactionRepo.save(reaction);
        return toResponse(saved);
    }

    @Transactional
    public ReactionResponseDTO updateReaction(@Valid ReactionRequestDTO request) {
        if (request.id() == null) {
            throw new AppException("ID required for update", 40003);
        }
        
        Reaction existingReaction = reactionRepo.findById(request.id())
                .orElseThrow(() -> new AppException("Reaction not found for update", 40402));
        
        if (!tweetRepo.existsById(request.tweetId())) {
            throw new AppException("Tweet not found for reaction update", 40405);
        }
        
        existingReaction.setTweetId(request.tweetId());
        existingReaction.setContent(request.content());
        
        Reaction updated = reactionRepo.save(existingReaction);
        return toResponse(updated);
    }

    @Transactional
    public void deleteReaction(@NotNull Long id) {
        if (!reactionRepo.existsById(id)) {
            throw new AppException("Reaction not found for deletion", 40402);
        }
        reactionRepo.deleteById(id);
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