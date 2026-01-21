package com.example.app.service;

import com.example.app.dto.ReactionRequestDTO;
import com.example.app.dto.ReactionResponseDTO;
import com.example.app.exception.AppException;
import com.example.app.model.Reaction;
import com.example.app.model.ReactionKey;
import com.example.app.repository.ReactionRepository;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class ReactionService {
    
    private final ReactionRepository reactionRepository;
    
    public ReactionService(ReactionRepository reactionRepository) {
        this.reactionRepository = reactionRepository;
    }
    
    // НОВЫЙ МЕТОД: Получить все реакции (осторожно!)
    public List<ReactionResponseDTO> getAllReactions() {
        // В Cassandra findAll() может быть медленным для больших таблиц
        List<Reaction> reactions = reactionRepository.findAll();
        return reactions.stream()
            .map(this::convertToDto)
            .collect(Collectors.toList());
    }
    
    // НОВЫЙ МЕТОД: Получить реакции по tweetId и country
    public List<ReactionResponseDTO> getReactionsByTweetId(String country, Long tweetId) {
        List<Reaction> reactions = reactionRepository.findByKeyCountryAndKeyTweetId(country, tweetId);
        return reactions.stream()
            .map(this::convertToDto)
            .collect(Collectors.toList());
    }
    
    // НОВЫЙ МЕТОД: Удалить все реакции твита
    public void deleteReactionsByTweetId(String country, Long tweetId) {
        reactionRepository.deleteByKeyCountryAndKeyTweetId(country, tweetId);
    }
    
    // Существующие методы (обновленные)
    @Transactional
    public ReactionResponseDTO createReaction(ReactionRequestDTO request) {
        Long newId = generateId();
        
        ReactionKey key = new ReactionKey(
            request.getCountry() != null ? request.getCountry() : "global",
            request.getTweetId(),
            newId
        );
        
        Reaction reaction = new Reaction(key, request.getContent());
        
        Reaction saved = reactionRepository.save(reaction);
        return convertToDto(saved);
    }
    
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
    
    @Transactional
    public ReactionResponseDTO updateReaction(String country, Long tweetId, Long id, ReactionRequestDTO request) {
        Reaction reaction = reactionRepository
            .findByKeyCountryAndKeyTweetIdAndKeyId(country, tweetId, id)
            .orElseThrow(() -> new AppException(
                "Reaction not found",
                HttpStatus.NOT_FOUND
            ));
        
        // Проверяем, что обновляем правильную реакцию
        if (!reaction.getTweetId().equals(request.getTweetId())) {
            throw new AppException("Cannot change tweetId of reaction", HttpStatus.BAD_REQUEST);
        }
        
        reaction.setContent(request.getContent());
        reaction.setUpdatedAt(LocalDateTime.now());
        
        Reaction updated = reactionRepository.save(reaction);
        return convertToDto(updated);
    }
    
    @Transactional
    public void deleteReaction(String country, Long tweetId, Long id) {
        if (!reactionRepository.existsByKeyCountryAndKeyTweetIdAndKeyId(country, tweetId, id)) {
            throw new AppException("Reaction not found", HttpStatus.NOT_FOUND);
        }
        
        ReactionKey key = new ReactionKey(country, tweetId, id);
        reactionRepository.deleteById(key);
    }
    
    private ReactionResponseDTO convertToDto(Reaction reaction) {
        ReactionResponseDTO dto = new ReactionResponseDTO();
        dto.setId(reaction.getId());
        dto.setTweetId(reaction.getTweetId());
        dto.setContent(reaction.getContent());
        dto.setCountry(reaction.getCountry());
        dto.setCreatedAt(reaction.getCreatedAt());
        dto.setUpdatedAt(reaction.getUpdatedAt());
        return dto;
    }
    
    private Long generateId() {
        // Используем временную метку как ID
        // В продакшене лучше использовать Cassandra TimeUUID или sequence
        return System.currentTimeMillis();
    }

}