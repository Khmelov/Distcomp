package com.example.task330.discussion.kafka;

import com.example.task330.discussion.dto.ReactionKafkaMessage;
import com.example.task330.discussion.dto.ReactionRequestTo;
import com.example.task330.discussion.dto.ReactionResponseTo;
import com.example.task330.discussion.service.ReactionService;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Locale;
import java.util.Optional;

// Получение и обработка команд из Kafka.
@Component
public class ReactionKafkaConsumer {

    private final ReactionService reactionService;
    private final ReactionKafkaProducer kafkaProducer;

    // Spring передаёт необходимые сервисы через конструктор.
    public ReactionKafkaConsumer(
            ReactionService reactionService,
            ReactionKafkaProducer kafkaProducer) {
        this.reactionService = reactionService;
        this.kafkaProducer = kafkaProducer;
    }

    // Получение сообщений из InTopic.
    @KafkaListener(topics = "InTopic", groupId = "discussion-group")
    public void receive(ReactionKafkaMessage message) {
        try {
            String operation = message.getOperation();

            if (operation == null) {
                throw new IllegalArgumentException("Operation is not specified");
            }

            switch (operation) {
                case "CREATE" -> create(message);
                case "GET_ALL" -> findAll(message);
                case "GET_BY_ID" -> findById(message);
                case "GET_BY_TOPIC" -> findByTopic(message);
                case "UPDATE" -> update(message);
                case "DELETE" -> delete(message);
                case "DELETE_BY_TOPIC" -> deleteByTopic(message);
                default -> throw new IllegalArgumentException(
                        "Unknown operation: " + operation
                );
            }
        } catch (Exception exception) {
            sendError(message, exception.getMessage());
        }
    }

    // Создание реакции и выполнение автоматической модерации.
    private void create(ReactionKafkaMessage message) {
        ReactionRequestTo request = createRequest(message);
        Long id = getReactionId(message);

        if (id == null) {
            throw new IllegalArgumentException("Reaction ID is not specified");
        }

        // Сначала сохраняем реакцию в обеих таблицах со статусом PENDING.
        reactionService.save(request, id, "PENDING");

        // После проверки текста устанавливаем окончательное состояние.
        String state = isAllowed(request.getContent())
                ? "APPROVE"
                : "DECLINE";

        Optional<ReactionResponseTo> result =
                reactionService.updateState(id, state);

        result.ifPresent(response -> {
            ReactionKafkaMessage reply = new ReactionKafkaMessage();
            reply.setRequestId(message.getRequestId());
            reply.setOperation("CREATE");
            reply.setReaction(response);
            reply.setReactionId(id);
            reply.setTopicId(response.getTopicId());
            reply.setState(response.getState());

            // Отправляем результат модерации в OutTopic.
            kafkaProducer.send(reply);
        });
    }

    // Получение всех реакций.
    private void findAll(ReactionKafkaMessage message) {
        ReactionKafkaMessage reply = createReply(message);
        reply.setReactions(reactionService.findAll());
        kafkaProducer.send(reply);
    }

    // Получение реакции по идентификатору.
    private void findById(ReactionKafkaMessage message) {
        Long id = getReactionId(message);

        if (id == null) {
            throw new IllegalArgumentException("Reaction ID is not specified");
        }

        ReactionResponseTo reaction = reactionService.findById(id)
                .orElseThrow(() -> new IllegalArgumentException(
                        "Reaction not found"
                ));

        ReactionKafkaMessage reply = createReply(message);
        reply.setReaction(reaction);
        kafkaProducer.send(reply);
    }

    // Получение реакций определённой темы.
    private void findByTopic(ReactionKafkaMessage message) {
        if (message.getTopicId() == null) {
            throw new IllegalArgumentException("Topic ID is not specified");
        }

        ReactionKafkaMessage reply = createReply(message);
        reply.setReactions(
                reactionService.findByTopicId(message.getTopicId())
        );
        kafkaProducer.send(reply);
    }

    // Изменение существующей реакции.
    private void update(ReactionKafkaMessage message) {
        ReactionRequestTo request = createRequest(message);
        Long id = getReactionId(message);

        if (id == null) {
            throw new IllegalArgumentException("Reaction ID is not specified");
        }

        ReactionResponseTo reaction = reactionService.update(id, request)
                .orElseThrow(() -> new IllegalArgumentException(
                        "Reaction not found"
                ));

        ReactionKafkaMessage reply = createReply(message);
        reply.setReaction(reaction);
        kafkaProducer.send(reply);
    }

    // Удаление реакции по идентификатору.
    private void delete(ReactionKafkaMessage message) {
        Long id = getReactionId(message);

        if (id == null || !reactionService.deleteById(id)) {
            throw new IllegalArgumentException("Reaction not found");
        }

        ReactionKafkaMessage reply = createReply(message);
        reply.setReactionId(id);
        kafkaProducer.send(reply);
    }

    // Удаление всех реакций определённой темы.
    private void deleteByTopic(ReactionKafkaMessage message) {
        if (message.getTopicId() == null) {
            throw new IllegalArgumentException("Topic ID is not specified");
        }

        reactionService.deleteByTopicId(message.getTopicId());

        ReactionKafkaMessage reply = createReply(message);
        reply.setTopicId(message.getTopicId());
        kafkaProducer.send(reply);
    }

    // Формирование объекта запроса из Kafka-сообщения.
    private ReactionRequestTo createRequest(ReactionKafkaMessage message) {
        if (message.getReaction() != null) {
            return new ReactionRequestTo(
                    message.getReaction().getTopicId(),
                    message.getReaction().getContent()
            );
        }

        return new ReactionRequestTo(
                message.getTopicId(),
                message.getContent()
        );
    }

    // Получение идентификатора реакции из сообщения.
    private Long getReactionId(ReactionKafkaMessage message) {
        if (message.getReactionId() != null) {
            return message.getReactionId();
        }

        if (message.getId() != null) {
            return message.getId();
        }

        if (message.getReaction() != null) {
            return message.getReaction().getId();
        }

        return null;
    }

    // Проверка текста реакции на наличие запрещённых слов.
    private boolean isAllowed(String content) {
        List<String> forbiddenWords = List.of(
                "spam",
                "scam",
                "мошенничество",
                "спам",
                "оскорбление"
        );

        String text = content.toLowerCase(Locale.ROOT);

        return forbiddenWords.stream().noneMatch(text::contains);
    }

    // Создание ответа с сохранением идентификатора запроса.
    private ReactionKafkaMessage createReply(ReactionKafkaMessage message) {
        ReactionKafkaMessage reply = new ReactionKafkaMessage();
        reply.setRequestId(message.getRequestId());
        reply.setOperation(message.getOperation());
        return reply;
    }

    // Отправка информации об ошибке в OutTopic.
    private void sendError(ReactionKafkaMessage message, String error) {
        if (message.getRequestId() == null) {
            return;
        }

        ReactionKafkaMessage reply = createReply(message);
        reply.setError(error == null ? "Unknown error" : error);
        kafkaProducer.send(reply);
    }
}
