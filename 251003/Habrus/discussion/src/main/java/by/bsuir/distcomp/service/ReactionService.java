package by.bsuir.distcomp.service;

import by.bsuir.distcomp.dto.mapper.ReactionMapper;
import by.bsuir.distcomp.dto.request.ReactionRequestTo;
import by.bsuir.distcomp.dto.response.ReactionResponseTo;
import by.bsuir.distcomp.entity.Reaction;
import by.bsuir.distcomp.entity.ReactionKey;
import by.bsuir.distcomp.repository.ReactionRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.stream.Collectors;

@Service
public class ReactionService {

    private final ReactionRepository reactionRepository;

    public ReactionService(ReactionRepository reactionRepository) {
        this.reactionRepository = reactionRepository;
    }

    public List<ReactionResponseTo> getAllReactions() {
        return reactionRepository.findAll()
                .stream()
                .map(ReactionMapper::toDto)
                .collect(Collectors.toList());
    }

    public ReactionResponseTo getReactionById(String country, Long id) {
        ReactionKey key = new ReactionKey(country, id);
        Reaction reaction = reactionRepository.findById(key)
                .orElseThrow(() -> new NoSuchElementException("Reaction not found"));
        return ReactionMapper.toDto(reaction);
    }

    @Transactional
    public ReactionResponseTo createReaction(String country, ReactionRequestTo reactionRequestTo) {
        Reaction entity = ReactionMapper.toEntity(country, reactionRequestTo);
        return ReactionMapper.toDto(reactionRepository.save(entity));
    }

    @Transactional
    public ReactionResponseTo updateReaction(String country, ReactionRequestTo reactionRequestTo) {
        ReactionKey key = new ReactionKey(country, reactionRequestTo.getId());

        if (!reactionRepository.existsById(key)) {
            throw new NoSuchElementException("Reaction not found to update");
        }

        Reaction updated = ReactionMapper.toEntity(country, reactionRequestTo);
        return ReactionMapper.toDto(reactionRepository.save(updated));
    }

    @Transactional
    public void deleteReaction(String country, Long id) {
        reactionRepository.deleteById(new ReactionKey(country, id));
    }

}
