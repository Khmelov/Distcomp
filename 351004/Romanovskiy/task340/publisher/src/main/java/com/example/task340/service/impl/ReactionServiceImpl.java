package com.example.task340.service.impl;

import com.example.task340.domain.dto.kafka.ReactionMessage;
import com.example.task340.domain.dto.request.ReactionRequestTo;
import com.example.task340.domain.dto.response.ReactionResponseTo;
import com.example.task340.domain.entity.Reaction;
import com.example.task340.domain.entity.ReactionState;
import com.example.task340.service.ReactionService;
import com.example.task340.kafka.ReactionProducer;
import com.example.task340.kafka.ReactionResponseManager;
import com.example.task340.mapper.ReactionMapper;
import com.example.task340.repository.ReactionRepository;
import com.example.task340.repository.TweetRepository;
import com.example.task340.exception.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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
            ReactionMessage response = responseManager.waitForResponse(savedReaction.getId(), 1);
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
    public ReactionResponseTo findById(Long id) {
        Reaction reaction = reactionRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Reaction not found with id: " + id));
        return reactionMapper.toResponseTo(reaction);
    }

    @Override
    @Transactional
    public ReactionResponseTo update(ReactionRequestTo request) {
        Reaction reaction = reactionRepository.findById(request.getId())
                .orElseThrow(() -> new EntityNotFoundException("Reaction not found with id: " + request.getId()));
        
        reaction.setContent(request.getContent());
        reaction.setTweetId(request.getTweetId());
        
        Reaction updated = reactionRepository.save(reaction);
        return reactionMapper.toResponseTo(updated);
    }

    @Override
    @Transactional
    public void deleteById(Long id) {
        if (!reactionRepository.existsById(id)) {
            throw new EntityNotFoundException("Reaction not found with id: " + id);
        }
        reactionRepository.deleteById(id);
    }
}