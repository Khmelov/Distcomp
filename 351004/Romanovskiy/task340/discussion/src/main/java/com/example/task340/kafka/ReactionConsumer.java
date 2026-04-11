package com.example.task340.kafka;

import com.example.task340.domain.dto.kafka.ReactionMessage;
import com.example.task340.domain.entity.Reaction;
import com.example.task340.domain.entity.ReactionState;
import com.example.task340.repository.ReactionRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class ReactionConsumer {

    private final ReactionRepository reactionRepository;
    private final ReactionProducer reactionProducer;
    private static final String TOPIC_IN = "in-topic";
    private static final String GROUP_ID = "discussion-group";

    @KafkaListener(
            topics = TOPIC_IN,
            groupId = GROUP_ID,
            containerFactory = "kafkaListenerContainerFactory"
    )
    public void consume(ReactionMessage message) {
        log.info("Consumed message: id={}, tweetId={}, country={}", 
                 message.getId(), message.getTweetId(), message.getCountry());
        
        // Выполняем модерацию
        String newState = performModeration(message);
        message.setState(newState);
        
        // Сохраняем в базу данных
        Reaction reaction = Reaction.builder()
                .id(message.getId())
                .tweetId(message.getTweetId())
                .country(message.getCountry())
                .content(message.getContent())
                .state(newState)
                .build();
        
        reactionRepository.save(reaction);
        log.info("Saved reaction with state={}", newState);
        
        // Отправляем результат обратно в publisher через out-topic
        reactionProducer.sendToOutTopic(message);
    }

    private String performModeration(ReactionMessage message) {
        // Простой алгоритм модерации на основе стоп-слов
        String[] stopWords = {"bad", "hate", "spam", "abuse"};
        String content = message.getContent().toLowerCase();
        
        for (String word : stopWords) {
            if (content.contains(word)) {
                return ReactionState.DECLINE.name();
            }
        }
        
        return ReactionState.APPROVE.name();
    }
}
