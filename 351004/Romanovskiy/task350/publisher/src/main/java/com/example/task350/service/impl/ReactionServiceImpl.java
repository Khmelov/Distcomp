package com.example.task350.service.impl;

import com.example.task350.domain.dto.kafka.ReactionMessage;
import com.example.task350.domain.dto.request.ReactionRequestTo;
import com.example.task350.domain.dto.response.ReactionResponseTo;
import com.example.task350.domain.entity.Reaction;
import com.example.task350.domain.entity.ReactionState;
import com.example.task350.service.ReactionService;
import com.example.task350.kafka.ReactionProducer;
import com.example.task350.kafka.ReactionResponseManager;
import com.example.task350.mapper.ReactionMapper;
import com.example.task350.repository.ReactionRepository;
import com.example.task350.repository.TweetRepository;
import com.example.task350.exception.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

// ИМПОРТЫ ДЛЯ КЕШИРОВАНИЯ
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;

import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class ReactionServiceImpl implements ReactionService {
    private final ReactionRepository reactionRepository;
    private final TweetRepository tweetRepository;
    private final ReactionProducer reactionProducer;
    private final ReactionResponseManager responseManager;
    private final ReactionMapper reactionMapper;

    @Override
    @Transactional
    @CachePut(value = "reactions", key = "#result.id") // Сохранит итоговый результат в Redis
    public ReactionResponseTo create(ReactionRequestTo request) {
        if (!tweetRepository.existsById(request.getTweetId())) {
            throw new EntityNotFoundException("Cannot create reaction: Tweet not found with id " + request.getTweetId());
        }
        
        // Создаем реакцию с состоянием PENDING
        Reaction reaction = Reaction.builder()
                .tweetId(request.getTweetId())
                .content(request.getContent())
                .state(ReactionState.PENDING)
                .build();
        
        Reaction savedReaction = reactionRepository.save(reaction);
        log.info("Created reaction with id={}, state={}", savedReaction.getId(), savedReaction.getState());
        
        // Регистрируем эту реакцию как ожидающую
        responseManager.registerPending(savedReaction.getId());
        
        // Отправляем сообщение в Kafka для обработки
        ReactionMessage message = ReactionMessage.builder()
                .id(savedReaction.getId())
                .tweetId(savedReaction.getTweetId())
                .content(savedReaction.getContent())
                .state(savedReaction.getState().name())
                .build();
        
        reactionProducer.sendToInTopic(message);
        
        // Ждем ответа из Kafka
        try {
            ReactionMessage response = responseManager.waitForResponse(savedReaction.getId(), 3);
            if (response != null) {
                // Обновляем статус в БД
                savedReaction.setState(ReactionState.valueOf(response.getState()));
                savedReaction = reactionRepository.save(savedReaction);
                log.info("Updated reaction state to={}", response.getState());
            } else {
                log.warn("No response received for reaction id={}", savedReaction.getId());
            }
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            log.error("Interrupted while waiting for response", e);
        } finally {
            responseManager.cleanup(savedReaction.getId());
        }
        
        return reactionMapper.toResponseTo(savedReaction);
    }

    @Override
    public List<ReactionResponseTo> findAll(int page, int size) {
        return reactionRepository.findAll(PageRequest.of(page, size))
                .getContent()
                .stream()
                .map(reactionMapper::toResponseTo)
                .collect(Collectors.toList());
    }

    @Override
    @Cacheable(value = "reactions", key = "#id") // Сначала ищет в Redis
    public ReactionResponseTo findById(Long id) {
        Reaction reaction = reactionRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Reaction not found with id: " + id));
        return reactionMapper.toResponseTo(reaction);
    }

    @Override
    @Transactional
    @CachePut(value = "reactions", key = "#request.id") // Обновит данные в Redis
    public ReactionResponseTo update(ReactionRequestTo request) {
        Reaction reaction = reactionRepository.findById(request.getId())
                .orElseThrow(() -> new EntityNotFoundException("Reaction not found"));
        
        reaction.setContent(request.getContent());
        Reaction updated = reactionRepository.save(reaction);

        // ОТПРАВЛЯЕМ В KAFKA ОБНОВЛЕНИЕ (для синхронизации с модулем Discussion)
        reactionProducer.sendToInTopic(ReactionMessage.builder()
                .id(updated.getId())
                .tweetId(updated.getTweetId())
                .content(updated.getContent())
                .state(updated.getState().name())
                .build());

        return reactionMapper.toResponseTo(updated);
    }

    @Override
    @Transactional
    @CacheEvict(value = "reactions", key = "#id") // Удалит из Redis
    public void deleteById(Long id) {
        Reaction reaction = reactionRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Reaction not found"));

        // ОТПРАВЛЯЕМ В KAFKA СИГНАЛ УДАЛЕНИЯ
        reactionProducer.sendToInTopic(ReactionMessage.builder()
                .id(reaction.getId())
                .tweetId(reaction.getTweetId())
                .state("DELETE")
                .build());

        reactionRepository.deleteById(id);
    }
}