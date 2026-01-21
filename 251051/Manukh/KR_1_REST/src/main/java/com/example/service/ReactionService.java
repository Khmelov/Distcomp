// ReactionService.java
package com.example.service;

import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;
import com.example.dto.request.ReactionRequestTo;
import com.example.dto.response.ReactionResponseTo;
import com.example.exception.NotFoundException;
import com.example.mapper.ReactionMapper;
import com.example.model.Reaction;
import com.example.repository.InMemoryReactionRepository;
import com.example.repository.InMemoryStoryRepository;

import java.util.List;

@Service
@Validated
public class ReactionService {

    @Autowired
    private InMemoryReactionRepository reactionRepository;

    @Autowired
    private InMemoryStoryRepository storyRepository;

    @Autowired
    private ReactionMapper reactionMapper;

    public List<ReactionResponseTo> getAllReactions() {
        return reactionMapper.toResponseList(reactionRepository.findAll());
    }

    public ReactionResponseTo getReactionById(Long id) {
        Reaction reaction = reactionRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Reaction not found with id: " + id, 40404));
        return reactionMapper.toResponse(reaction);
    }

    public ReactionResponseTo createReaction(@Valid ReactionRequestTo request) {
        // Check if story exists
        if (!storyRepository.existsById(request.getStoryId())) {
            throw new NotFoundException("Story not found with id: " + request.getStoryId(), 40402);
        }

        Reaction reaction = reactionMapper.toEntity(request);
        Reaction savedReaction = reactionRepository.save(reaction);
        return reactionMapper.toResponse(savedReaction);
    }

    public ReactionResponseTo updateReaction(Long id, @Valid ReactionRequestTo request) {
        Reaction existingReaction = reactionRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Reaction not found with id: " + id, 40404));

        // Check if story exists
        if (!storyRepository.existsById(request.getStoryId())) {
            throw new NotFoundException("Story not found with id: " + request.getStoryId(), 40402);
        }

        existingReaction.setStoryId(request.getStoryId());
        existingReaction.setContent(request.getContent());

        Reaction updatedReaction = reactionRepository.update(existingReaction);
        return reactionMapper.toResponse(updatedReaction);
    }

    public void deleteReaction(Long id) {
        if (!reactionRepository.existsById(id)) {
            throw new NotFoundException("Reaction not found with id: " + id, 40404);
        }
        reactionRepository.deleteById(id);
    }

    public List<ReactionResponseTo> getReactionsByStoryId(Long storyId) {
        if (!storyRepository.existsById(storyId)) {
            throw new NotFoundException("Story not found with id: " + storyId, 40402);
        }

        List<Reaction> reactions = reactionRepository.findByStoryId(storyId);
        return reactionMapper.toResponseList(reactions);
    }
}