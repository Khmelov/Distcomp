package com.example.publisher.service;

import com.example.publisher.dto.request.ReactionRequestTo;
import com.example.publisher.dto.response.ReactionResponseTo;
import com.example.publisher.entity.Reaction;
import com.example.publisher.entity.Story;
import com.example.publisher.exception.NotFoundException;
import com.example.publisher.mapper.ReactionMapper;
import com.example.publisher.repository.ReactionRepository;
import com.example.publisher.repository.StoryRepository;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.annotation.Validated;

import java.time.LocalDateTime;
import java.util.List;

@Service
@Validated
@Transactional(readOnly = true)
public class ReactionService {

    @Autowired
    private ReactionRepository reactionRepository;

    @Autowired
    private StoryRepository storyRepository;

    @Autowired
    private ReactionMapper reactionMapper;

    public List<ReactionResponseTo> getAllReactions() {
        return reactionMapper.toResponseList(reactionRepository.findAll());
    }

    public Page<ReactionResponseTo> getAllReactions(Pageable pageable) {
        return reactionRepository.findAll(pageable)
                .map(reactionMapper::toResponse);
    }

    public ReactionResponseTo getReactionById(Long id) {
        Reaction reaction = reactionRepository.findByIdWithStory(id)
                .orElseThrow(() -> new NotFoundException("Reaction not found with id: " + id, 40404));
        return reactionMapper.toResponse(reaction);
    }

    @Transactional
    public ReactionResponseTo createReaction(@Valid ReactionRequestTo request) {
        Story story = storyRepository.findById(request.getStoryId())
                .orElseThrow(() -> new NotFoundException("Story not found with id: " + request.getStoryId(), 40402));

        Reaction reaction = reactionMapper.toEntity(request);
        reaction.setStory(story);
        reaction.setCreatedAt(LocalDateTime.now());
        reaction.setModifiedAt(LocalDateTime.now());

        Reaction savedReaction = reactionRepository.save(reaction);
        return reactionMapper.toResponse(savedReaction);
    }

    @Transactional
    public ReactionResponseTo updateReaction(Long id, @Valid ReactionRequestTo request) {
        Reaction existingReaction = reactionRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Reaction not found with id: " + id, 40404));

        Story story = storyRepository.findById(request.getStoryId())
                .orElseThrow(() -> new NotFoundException("Story not found with id: " + request.getStoryId(), 40402));

        reactionMapper.updateEntity(request, existingReaction);
        existingReaction.setStory(story);
        existingReaction.setModifiedAt(LocalDateTime.now());

        Reaction updatedReaction = reactionRepository.save(existingReaction);
        return reactionMapper.toResponse(updatedReaction);
    }

    @Transactional
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

    public Page<ReactionResponseTo> getReactionsByStoryId(Long storyId, Pageable pageable) {
        if (!storyRepository.existsById(storyId)) {
            throw new NotFoundException("Story not found with id: " + storyId, 40402);
        }

        return reactionRepository.findByStoryId(storyId, pageable)
                .map(reactionMapper::toResponse);
    }

    public Page<ReactionResponseTo> searchReactions(Long storyId, String content, Pageable pageable) {
        if (storyId != null && content != null) {
            return reactionRepository.findByStoryIdAndContentContaining(storyId, content, pageable)
                    .map(reactionMapper::toResponse);
        } else if (storyId != null) {
            return reactionRepository.findByStoryId(storyId, pageable)
                    .map(reactionMapper::toResponse);
        } else {
            return reactionRepository.findAll(pageable)
                    .map(reactionMapper::toResponse);
        }
    }

    public Long countReactionsByStoryId(Long storyId) {
        if (!storyRepository.existsById(storyId)) {
            throw new NotFoundException("Story not found with id: " + storyId, 40402);
        }
        return reactionRepository.countByStoryId(storyId);
    }
}