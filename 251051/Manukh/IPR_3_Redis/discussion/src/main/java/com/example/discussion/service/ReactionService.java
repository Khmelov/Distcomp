package com.example.discussion.service;

import com.example.discussion.entity.Reaction;
import com.example.discussion.repository.ReactionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
public class ReactionService {

    @Autowired
    private ReactionRepository reactionRepository;

    public List<Reaction> getAllReactions() {
        return reactionRepository.findAll();
    }

    public Reaction getReactionById(String id) {
        return reactionRepository.findById(id).orElse(null);
    }

    public Reaction createReaction(Long storyId, String content) {
        Reaction reaction = new Reaction();
        reaction.setStoryId(storyId);
        reaction.setContent(content);
        return reactionRepository.save(reaction);
    }

    public void deleteReaction(String id) {
        reactionRepository.deleteById(id);
    }

    public List<Reaction> getReactionsByStoryId(Long storyId) {
        return reactionRepository.findByStoryId(storyId);
    }
}