package com.example.task361.service.impl;

import com.example.task361.domain.dto.request.ReactionRequestTo;
import com.example.task361.domain.dto.response.ReactionResponseTo;
import com.example.task361.domain.entity.Reaction;
import com.example.task361.exception.EntityNotFoundException;
import com.example.task361.mapper.ReactionMapper;
import com.example.task361.repository.ReactionRepository;
import com.example.task361.service.ReactionService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Random;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ReactionServiceImpl implements ReactionService {

    private final ReactionRepository reactionRepository;
    private final ReactionMapper reactionMapper;

    @Override
    public ReactionResponseTo create(ReactionRequestTo request) {
        Reaction reaction = reactionMapper.toEntity(request);
        
        // Согласно ТЗ, нужно поле country. Если его нет в запросе, ставим дефолт
        if (reaction.getCountry() == null) {
            reaction.setCountry("Global");
        }
        
        // Генерируем случайный ID (Cassandra не умеет в auto-increment)
        reaction.setId(Math.abs(new Random().nextLong()));

        Reaction saved = reactionRepository.save(reaction);
        return reactionMapper.toResponse(saved);
    }

    @Override
    public List<ReactionResponseTo> findAll(int page, int size) {
        return reactionRepository.findAll(PageRequest.of(page, size))
                .getContent().stream()
                .map(reactionMapper::toResponse)
                .collect(Collectors.toList());
    }

    @Override
    public ReactionResponseTo findById(Long id) {
        // Поиск в Cassandra без полного ключа может быть медленным, 
        // но для LR этого достаточно.
        return reactionRepository.findAll().stream()
                .filter(r -> r.getId().equals(id))
                .findFirst()
                .map(reactionMapper::toResponse)
                .orElseThrow(() -> new EntityNotFoundException("Reaction not found with id: " + id));
    }

    @Override
    public ReactionResponseTo update(ReactionRequestTo request) {
        // В Cassandra важно найти существующую запись или знать все ключи
        Reaction reaction = reactionMapper.toEntity(request);
        
        // Если в запросе на обновление нет страны, ставим ту же, что при создании
        if (reaction.getCountry() == null) {
            reaction.setCountry("Global");
        }
        
        // Сохраняем (в Cassandra это перезапишет строку с тем же ID)
        Reaction saved = reactionRepository.save(reaction);
        return reactionMapper.toResponse(saved);
    }

    @Override
    public void deleteById(Long id) {
        // 1. Ищем реакцию по ID (так как в Cassandra это не первичный ключ, используем поток)
        Reaction reaction = reactionRepository.findAll().stream()
                .filter(r -> r.getId().equals(id))
                .findFirst()
                .orElseThrow(() -> new EntityNotFoundException("Reaction not found with id: " + id));

        // 2. Если нашли — удаляем
        reactionRepository.delete(reaction);
    }
}