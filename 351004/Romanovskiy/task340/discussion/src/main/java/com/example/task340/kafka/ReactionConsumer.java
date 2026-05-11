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
        log.info("Consumed message: id={}, state={}", message.getId(), message.getState());

        // 1. Обработка УДАЛЕНИЯ
        if ("DELETE".equals(message.getState())) {
            reactionRepository.findAll().stream()
                    .filter(r -> r.getId().equals(message.getId()))
                    .findFirst()
                    .ifPresent(reactionRepository::delete);
            log.info("Deleted reaction id={} from Cassandra", message.getId());
            return;
        }

        // 2. Обработка СОЗДАНИЯ / ОБНОВЛЕНИЯ
        String state = message.getState();
        
        // Если пришло PENDING — значит это новая реакция, требующая модерации
        if ("PENDING".equals(state)) {
            state = performModeration(message);
        }

        // Ключ партиции в Cassandra не может быть null
        String country = (message.getCountry() == null || message.getCountry().isEmpty()) 
                         ? "Global" : message.getCountry();

        Reaction reaction = Reaction.builder()
                .id(message.getId())
                .tweetId(message.getTweetId())
                .country(country)
                .content(message.getContent())
                .state(state)
                .build();

        reactionRepository.save(reaction);
        log.info("Saved/Updated reaction id={} in Cassandra with state={}", message.getId(), state);
        
        // Отправляем результат обратно в Publisher только для новых реакций
        if ("PENDING".equals(message.getState())) {
            message.setState(state);
            message.setCountry(country);
            reactionProducer.sendToOutTopic(message);
        }
    }

    // ТОТ САМЫЙ МЕТОД, КОТОРОГО НЕ ХВАТАЛО
    private String performModeration(ReactionMessage message) {
        if (message.getContent() == null) return ReactionState.APPROVE.name();
        
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