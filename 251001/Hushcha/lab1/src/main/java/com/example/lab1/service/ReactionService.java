package com.example.lab1.service;

import com.example.lab1.controller.exception.EntityNotFoundException;
import com.example.lab1.dto.ReactionRequestTo;
import com.example.lab1.dto.ReactionResponseTo;
import com.example.lab1.entity.Reaction;
import com.example.lab1.mapper.ReactionMapper;
import com.example.lab1.repository.EntityRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ReactionService implements EntityService<ReactionRequestTo, ReactionResponseTo> {

    private final EntityRepository<Reaction> reactionRepository;
    private final ReactionMapper reactionMapper = ReactionMapper.INSTANCE;

    @Override
    public ReactionResponseTo create(ReactionRequestTo entityDTO) {
        Reaction reaction = reactionMapper.toEntity(entityDTO);
        Reaction savedReaction = reactionRepository.create(reaction);
        return reactionMapper.toDTO(savedReaction);
    }

    @Override
    public ReactionResponseTo getById(Long id) {
        return reactionRepository.findById(id)
                .map(reactionMapper::toDTO)
                .orElseThrow(() -> new EntityNotFoundException("Reaction with ID " + id + " not found"));
    }

    @Override
    public List<ReactionResponseTo> getAll() {
        return reactionRepository.findAll()
                .stream()
                .map(reactionMapper::toDTO)
                .collect(Collectors.toList());
    }

    @Override
    public ReactionResponseTo update(ReactionRequestTo entityDTO) {
        Reaction reaction = reactionMapper.toEntity(entityDTO);
        Reaction updatedReaction = reactionRepository.update(reaction);
        return reactionMapper.toDTO(updatedReaction);
    }

    @Override
    public void deleteById(Long id) {
        if (reactionRepository.findById(id).isEmpty()) {
            throw new EntityNotFoundException("Reaction with ID " + id + " not found");
        }
        reactionRepository.deleteById(id);
    }
}

