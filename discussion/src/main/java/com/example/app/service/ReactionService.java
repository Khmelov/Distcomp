package com.example.app.service;

import com.example.app.dto.ReactionRequestDTO;
import com.example.app.dto.ReactionResponseDTO;
import com.example.app.dto.kafka.KafkaReactionMessage;
import com.example.app.exception.AppException;
import com.example.app.kafka.KafkaProducerService;
import com.example.app.model.Reaction;
import com.example.app.model.ReactionKey;
import com.example.app.repository.ReactionRepository;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class ReactionService {
    
    private final ReactionRepository reactionRepository;
    private final KafkaProducerService kafkaProducerService;
    private final ModerationService moderationService; // Для автоматической модерации
    
    // Список стоп-слов для модерации
    private static final List<String> BAD_WORDS = Arrays.asList(
        "spam", "scam", "hate", "violence", "illegal"
    );
    
    public ReactionService(ReactionRepository reactionRepository,
                          KafkaProducerService kafkaProducerService,
                          ModerationService moderationService) {
        this.reactionRepository = reactionRepository;
        this.kafkaProducerService = kafkaProducerService;
        this.moderationService = moderationService;
    }
    
    // Получить все реакции
    public List<ReactionResponseDTO> getAllReactions() {
        List<Reaction> reactions = reactionRepository.findAll();
        return reactions.stream()
            .map(this::convertToDto)
            .collect(Collectors.toList());
    }
    
    // Получить реакции по tweetId и country
    public List<ReactionResponseDTO> getReactionsByTweetId(String country, Long tweetId) {
        List<Reaction> reactions = reactionRepository.findByKeyCountryAndKeyTweetId(country, tweetId);
        return reactions.stream()
            .map(this::convertToDto)
            .collect(Collectors.toList());
    }
    
    // Получить только APPROVED реакции для твита
    public List<ReactionResponseDTO> getApprovedReactionsByTweetId(String country, Long tweetId) {
        List<Reaction> reactions = reactionRepository.findByKeyCountryAndKeyTweetId(country, tweetId);
        return reactions.stream()
            .filter(Reaction::isApproved)
            .map(this::convertToDto)
            .collect(Collectors.toList());
    }
    
    // Удалить все реакции твита
    public void deleteReactionsByTweetId(String country, Long tweetId) {
        reactionRepository.deleteByKeyCountryAndKeyTweetId(country, tweetId);
    }
    
    // Создать реакцию (синхронно через REST)
    @Transactional
    public ReactionResponseDTO createReaction(ReactionRequestDTO request) {
        Long newId = generateId();
        
        ReactionKey key = new ReactionKey(
            request.getCountry() != null ? request.getCountry() : "global",
            request.getTweetId(),
            newId
        );
        
        Reaction reaction = new Reaction(key, request.getContent());
        reaction.setState(request.getState() != null ? request.getState() : "PENDING");
        
        // Проводим автоматическую модерацию
        String moderationResult = moderateContent(request.getContent());
        reaction.setState(moderationResult);
        
        Reaction saved = reactionRepository.save(reaction);
        
        ReactionResponseDTO response = convertToDto(saved);
        
        // Отправляем сообщение в Kafka о создании реакции
        kafkaProducerService.sendReactionCreated(response);
        
        return response;
    }
    
    // Создать реакцию асинхронно через Kafka
    @Transactional
    public ReactionResponseDTO createReactionFromKafka(ReactionRequestDTO request) {
        Long newId = generateId();
        
        ReactionKey key = new ReactionKey(
            request.getCountry() != null ? request.getCountry() : "global",
            request.getTweetId(),
            newId
        );
        
        Reaction reaction = new Reaction(key, request.getContent());
        reaction.setState("PENDING"); // При создании через Kafka всегда PENDING
        
        // Автоматическая модерация
        String moderationResult = moderateContent(request.getContent());
        reaction.setState(moderationResult);
        
        Reaction saved = reactionRepository.save(reaction);
        
        ReactionResponseDTO response = convertToDto(saved);
        
        // Отправляем результат модерации в out-topic
        kafkaProducerService.sendModerationResult(response);
        
        return response;
    }
    
    // Получить реакцию по ID
    public ReactionResponseDTO getReactionById(String country, Long tweetId, Long id) {
        Reaction reaction = reactionRepository
            .findByKeyCountryAndKeyTweetIdAndKeyId(country, tweetId, id)
            .orElseThrow(() -> new AppException(
                "Reaction not found with id: " + id + 
                ", tweetId: " + tweetId + 
                ", country: " + country,
                HttpStatus.NOT_FOUND
            ));
        
        return convertToDto(reaction);
    }
    
    // Обновить реакцию
    @Transactional
    public ReactionResponseDTO updateReaction(String country, Long tweetId, Long id, ReactionRequestDTO request) {
        Reaction reaction = reactionRepository
            .findByKeyCountryAndKeyTweetIdAndKeyId(country, tweetId, id)
            .orElseThrow(() -> new AppException(
                "Reaction not found",
                HttpStatus.NOT_FOUND
            ));
        
        if (!reaction.getTweetId().equals(request.getTweetId())) {
            throw new AppException("Cannot change tweetId of reaction", HttpStatus.BAD_REQUEST);
        }
        
        reaction.setContent(request.getContent());
        reaction.setUpdatedAt(LocalDateTime.now());
        
        // Если изменился контент, проводим повторную модерацию
        if (!reaction.getContent().equals(request.getContent())) {
            String moderationResult = moderateContent(request.getContent());
            reaction.setState(moderationResult);
        }
        
        Reaction updated = reactionRepository.save(reaction);
        
        ReactionResponseDTO response = convertToDto(updated);
        
        // Отправляем сообщение в Kafka об обновлении
        kafkaProducerService.sendReactionUpdated(response);
        
        return response;
    }
    
    // Удалить реакцию
    @Transactional
    public void deleteReaction(String country, Long tweetId, Long id) {
        if (!reactionRepository.existsByKeyCountryAndKeyTweetIdAndKeyId(country, tweetId, id)) {
            throw new AppException("Reaction not found", HttpStatus.NOT_FOUND);
        }
        
        ReactionKey key = new ReactionKey(country, tweetId, id);
        Reaction reaction = reactionRepository.findById(key)
            .orElseThrow(() -> new AppException("Reaction not found", HttpStatus.NOT_FOUND));
        
        reactionRepository.deleteById(key);
        
        // Отправляем сообщение в Kafka об удалении
        kafkaProducerService.sendReactionDeleted(convertToDto(reaction));
    }
    
    // Модерация реакции (ручная или автоматическая)
    @Transactional
    public ReactionResponseDTO moderateReaction(String country, Long tweetId, Long id, String newState) {
        if (!"APPROVE".equals(newState) && !"DECLINE".equals(newState)) {
            throw new AppException("Invalid state. Use APPROVE or DECLINE", HttpStatus.BAD_REQUEST);
        }
        
        Reaction reaction = reactionRepository
            .findByKeyCountryAndKeyTweetIdAndKeyId(country, tweetId, id)
            .orElseThrow(() -> new AppException("Reaction not found", HttpStatus.NOT_FOUND));
        
        reaction.setState(newState);
        reaction.setUpdatedAt(LocalDateTime.now());
        
        Reaction moderated = reactionRepository.save(reaction);
        
        ReactionResponseDTO response = convertToDto(moderated);
        
        // Отправляем результат модерации в Kafka
        kafkaProducerService.sendModerationResult(response);
        
        return response;
    }
    
    // Автоматическая модерация контента
    private String moderateContent(String content) {
        String lowerContent = content.toLowerCase();
        
        // Проверяем на стоп-слова
        for (String badWord : BAD_WORDS) {
            if (lowerContent.contains(badWord)) {
                return "DECLINE";
            }
        }
        
        // Дополнительные правила модерации
        if (content.length() < 5) {
            return "DECLINE"; // Слишком короткое сообщение
        }
        
        if (content.length() > 1000) {
            return "DECLINE"; // Слишком длинное сообщение
        }
        
        // Проверка на спам (повторяющиеся символы)
        if (containsSpam(content)) {
            return "DECLINE";
        }
        
        return "APPROVE";
    }
    
    // Проверка на спам
    private boolean containsSpam(String content) {
        // Простая проверка на повторяющиеся символы
        if (content.length() > 10) {
            for (int i = 0; i < content.length() - 5; i++) {
                String substring = content.substring(i, i + 5);
                if (content.indexOf(substring, i + 1) != -1) {
                    return true;
                }
            }
        }
        return false;
    }
    
    // Конвертация в DTO
    private ReactionResponseDTO convertToDto(Reaction reaction) {
        ReactionResponseDTO dto = new ReactionResponseDTO();
        dto.setId(reaction.getId());
        dto.setTweetId(reaction.getTweetId());
        dto.setContent(reaction.getContent());
        dto.setCountry(reaction.getCountry());
        dto.setState(reaction.getState());
        dto.setCreatedAt(reaction.getCreatedAt());
        dto.setUpdatedAt(reaction.getUpdatedAt());
        return dto;
    }
    
    private Long generateId() {
        return System.currentTimeMillis();
    }
    
    // Вспомогательный сервис для модерации (можно вынести в отдельный класс)
    @Service
    public static class ModerationService {
        public String moderate(String content) {
            // Логика модерации
            if (content.contains("spam")) {
                return "DECLINE";
            }
            return "APPROVE";
        }
    }
}