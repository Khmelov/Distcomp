package com.example.task330.publisher.kafka;

import com.example.task330.publisher.dto.ReactionKafkaMessage;
import com.example.task330.publisher.dto.ReactionRequestTo;
import com.example.task330.publisher.dto.ReactionResponseTo;
import com.example.task330.publisher.service.RedisCacheService;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

// Сервис обеспечивает обмен сообщениями между Publisher и Discussion через Kafka.
@Service
public class ReactionKafkaService {

    private static final String IN_TOPIC = "InTopic";
    private static final long RESPONSE_TIMEOUT = 1;

    private final KafkaTemplate<String, ReactionKafkaMessage> kafkaTemplate;
    private final RedisCacheService redisCacheService;

    // Хранение ожидающих ответов для одновременных запросов.
    private final ConcurrentHashMap<String, CompletableFuture<ReactionKafkaMessage>>
            pendingRequests = new ConcurrentHashMap<>();

    // Spring передаёт необходимые компоненты через конструктор.
    public ReactionKafkaService(
            KafkaTemplate<String, ReactionKafkaMessage> kafkaTemplate,
            RedisCacheService redisCacheService) {
        this.kafkaTemplate = kafkaTemplate;
        this.redisCacheService = redisCacheService;
    }

    // Создание реакции с начальным состоянием PENDING без ожидания модерации.
    public ReactionResponseTo create(ReactionRequestTo request) {
        Long id = UUID.randomUUID().getMostSignificantBits() & Long.MAX_VALUE;

        ReactionResponseTo reaction = new ReactionResponseTo(
                id,
                request.getTopicId(),
                request.getContent(),
                "PENDING"
        );

        ReactionKafkaMessage message = new ReactionKafkaMessage();
        message.setRequestId(UUID.randomUUID().toString());
        message.setOperation("CREATE");
        message.setReactionId(id);
        message.setTopicId(request.getTopicId());
        message.setContent(request.getContent());
        message.setReaction(reaction);

        // Удаляем устаревший список реакций перед созданием новой.
        redisCacheService.deleteByPattern("reactions:*");

        // Отправляем реакцию в Discussion без ожидания модерации.
        send(message);

        return reaction;
    }

    // Получение всех реакций через Discussion.
    public List<ReactionResponseTo> findAll() {
        ReactionKafkaMessage message = new ReactionKafkaMessage();
        message.setOperation("GET_ALL");

        ReactionKafkaMessage response = sendAndWait(message);
        checkError(response);

        return response.getReactions() == null
                ? List.of()
                : response.getReactions();
    }

    // Получение реакции по идентификатору.
    public Optional<ReactionResponseTo> findById(Long id) {
        ReactionKafkaMessage message = new ReactionKafkaMessage();
        message.setOperation("GET_BY_ID");
        message.setReactionId(id);

        ReactionKafkaMessage response = sendAndWait(message);

        if (response.getError() != null) {
            if ("Reaction not found".equals(response.getError())) {
                return Optional.empty();
            }
            checkError(response);
        }

        return Optional.ofNullable(response.getReaction());
    }

    // Получение реакций определённой темы.
    public List<ReactionResponseTo> findByTopicId(Long topicId) {
        ReactionKafkaMessage message = new ReactionKafkaMessage();
        message.setOperation("GET_BY_TOPIC");
        message.setTopicId(topicId);

        ReactionKafkaMessage response = sendAndWait(message);
        checkError(response);

        return response.getReactions() == null
                ? List.of()
                : response.getReactions();
    }

    // Обновление реакции через Discussion.
    public Optional<ReactionResponseTo> update(
            Long id,
            ReactionRequestTo request) {

        ReactionKafkaMessage message = new ReactionKafkaMessage();
        message.setOperation("UPDATE");
        message.setReactionId(id);
        message.setTopicId(request.getTopicId());
        message.setContent(request.getContent());
        message.setReaction(new ReactionResponseTo(
                id,
                request.getTopicId(),
                request.getContent(),
                "APPROVE"
        ));

        ReactionKafkaMessage response = sendAndWait(message);

        if (response.getError() != null) {
            if ("Reaction not found".equals(response.getError())) {
                return Optional.empty();
            }
            checkError(response);
        }

        Optional<ReactionResponseTo> updatedReaction =
                Optional.ofNullable(response.getReaction());

        // Сохраняем обновлённую реакцию и очищаем список.
        updatedReaction.ifPresent(reaction -> {
            redisCacheService.save("reaction:" + id, reaction);
            redisCacheService.deleteByPattern("reactions:*");
        });

        return updatedReaction;
    }

    // Удаление реакции по идентификатору.
    public boolean deleteById(Long id) {
        ReactionKafkaMessage message = new ReactionKafkaMessage();
        message.setOperation("DELETE");
        message.setReactionId(id);

        ReactionKafkaMessage response = sendAndWait(message);

        if ("Reaction not found".equals(response.getError())) {
            return false;
        }

        checkError(response);

        // Удаляем реакцию и список из кеша.
        redisCacheService.delete("reaction:" + id);
        redisCacheService.deleteByPattern("reactions:*");

        return true;
    }

    // Удаление всех реакций определённой темы.
    public void deleteByTopicId(Long topicId) {
        ReactionKafkaMessage message = new ReactionKafkaMessage();
        message.setOperation("DELETE_BY_TOPIC");
        message.setTopicId(topicId);

        ReactionKafkaMessage response = sendAndWait(message);
        checkError(response);

        // Очищаем кеш реакций после удаления данных темы.
        redisCacheService.deleteByPattern("reaction:*");
        redisCacheService.deleteByPattern("reactions:*");
    }

    // Отправка сообщения в InTopic.
    private void send(ReactionKafkaMessage message) {
        String key = message.getTopicId() == null
                ? "request"
                : message.getTopicId().toString();

        kafkaTemplate.send(IN_TOPIC, key, message);
    }

    // Отправка команды и ожидание ответа от Discussion.
    private ReactionKafkaMessage sendAndWait(ReactionKafkaMessage message) {
        String requestId = UUID.randomUUID().toString();
        message.setRequestId(requestId);

        CompletableFuture<ReactionKafkaMessage> future =
                new CompletableFuture<>();

        pendingRequests.put(requestId, future);

        try {
            send(message);

            return future.get(RESPONSE_TIMEOUT, TimeUnit.SECONDS);

        } catch (TimeoutException exception) {
            throw new IllegalStateException(
                    "Discussion did not respond within one second",
                    exception
            );
        } catch (InterruptedException exception) {
            Thread.currentThread().interrupt();
            throw new IllegalStateException(
                    "Request to Discussion was interrupted",
                    exception
            );
        } catch (ExecutionException exception) {
            throw new IllegalStateException(
                    "Error receiving response from Discussion",
                    exception.getCause()
            );
        } finally {
            pendingRequests.remove(requestId);
        }
    }

    // Получение ответов из OutTopic и сопоставление их с запросами.
    @KafkaListener(topics = "OutTopic", groupId = "publisher-group")
    public void receive(ReactionKafkaMessage message) {
        if (message.getRequestId() == null) {
            return;
        }

        CompletableFuture<ReactionKafkaMessage> future =
                pendingRequests.get(message.getRequestId());

        if (future != null) {
            future.complete(message);
            return;
        }

        // Сохраняем окончательный результат асинхронного создания реакции.
        if ("CREATE".equals(message.getOperation())
                && message.getReaction() != null) {

            ReactionResponseTo reaction = message.getReaction();

            redisCacheService.save(
                    "reaction:" + reaction.getId(),
                    reaction
            );

            // Обновляем кеш списка реакций.
            redisCacheService.deleteByPattern("reactions:*");
        }
    }

    // Преобразование ошибки Discussion в исключение.
    private void checkError(ReactionKafkaMessage response) {
        if (response.getError() != null) {
            throw new IllegalStateException(response.getError());
        }
    }
}
