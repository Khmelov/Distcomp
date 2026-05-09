package by.bsuir.task310.service;

import by.bsuir.task310.dto.ReactionRequestTo;
import by.bsuir.task310.dto.ReactionResponseTo;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@Service
public class ReactionKafkaService {

    private final ReactionService reactionService;
    private final KafkaTemplate<String, ReactionResponseTo> kafkaTemplate;

    public ReactionKafkaService(ReactionService reactionService,
                                KafkaTemplate<String, ReactionResponseTo> kafkaTemplate) {
        this.reactionService = reactionService;
        this.kafkaTemplate = kafkaTemplate;
    }

    @KafkaListener(
            topics = "InTopic",
            groupId = "discussion-group",
            containerFactory = "reactionRequestKafkaListenerContainerFactory"
    )
    public void listenCreate(ReactionRequestTo requestTo) {
        ReactionResponseTo responseTo = reactionService.create(requestTo);
        kafkaTemplate.send("OutTopic", responseTo);
    }
}