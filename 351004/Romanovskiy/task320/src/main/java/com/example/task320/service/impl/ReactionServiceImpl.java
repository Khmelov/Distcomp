package com.example.task320.service.impl;

import com.example.task320.domain.dto.request.ReactionRequestTo;
import com.example.task320.domain.dto.response.MarkerResponseTo;
import com.example.task320.domain.dto.response.ReactionResponseTo;
import com.example.task320.domain.entity.Reaction;
import com.example.task320.exception.EntityNotFoundException;
import com.example.task320.mapper.MarkerMapper;
import com.example.task320.mapper.ReactionMapper;
import com.example.task320.repository.MarkerRepository;
import com.example.task320.repository.ReactionRepository;
import com.example.task320.service.ReactionService;
import lombok.RequiredArgsConstructor;

import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ReactionServiceImpl implements ReactionService {

    private final ReactionRepository reactionRepository;
    private final ReactionMapper reactionMapper;

    @Override
    public ReactionResponseTo create(ReactionRequestTo request) {
        Reaction reaction = reactionMapper.toEntity(request);
        return reactionMapper.toResponse(reactionRepository.save(reaction));
    }

    @Override
    public List<ReactionResponseTo> findAll(int page, int size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by("id").ascending());
        return reactionRepository.findAll(pageable).getContent().stream()
                .map(reactionMapper::toResponse)
                .collect(Collectors.toList());
    }

    @Override
    public ReactionResponseTo findById(Long id) {
        Reaction reaction = reactionRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Reaction not found with this id"));
        return reactionMapper.toResponse(reaction);
    }

    @Override
    public ReactionResponseTo update(ReactionRequestTo request) {
        if (!reactionRepository.existsById(request.getId())) {
            throw new RuntimeException("Cannot update: Reaction not found");
        }
        Reaction reaction = reactionMapper.toEntity(request);
        return reactionMapper.toResponse(reactionRepository.save(reaction));
    }

    @Override
    public void deleteById(Long id) {
        if (!reactionRepository.existsById(id)) {
            throw new EntityNotFoundException("Reaction not found with id: " + id);
        }
        reactionRepository.deleteById(id);
    }
}