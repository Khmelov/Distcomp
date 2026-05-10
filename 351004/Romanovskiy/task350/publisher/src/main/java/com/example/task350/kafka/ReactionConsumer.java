package com.example.task350.kafka;

import com.example.task350.domain.dto.kafka.ReactionMessage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class ReactionConsumer {

    private final ReactionResponseManager responseManager;
    private static final String TOPIC_OUT = "out-topic";
    private static final String GROUP_ID = "publisher-group";

    @KafkaListener(
            topics = TOPIC_OUT,
            groupId = GROUP_ID,
            containerFactory = "kafkaListenerContainerFactory"
    )
    public void consume(ReactionMessage message) {
        log.info("Consumed message: id={}, tweetId={}, state={}", 
                 message.getId(), message.getTweetId(), message.getState());
        responseManager.storeResponse(message);
    }
}
