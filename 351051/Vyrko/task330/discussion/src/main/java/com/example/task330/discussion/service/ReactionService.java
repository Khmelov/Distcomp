
package com.example.task330.discussion.service;

import com.example.task330.discussion.dto.ReactionRequestTo;
import com.example.task330.discussion.dto.ReactionResponseTo;
import com.example.task330.discussion.model.Reaction;
import com.example.task330.discussion.model.ReactionByTopic;
import com.example.task330.discussion.model.ReactionByTopicKey;
import com.example.task330.discussion.repository.ReactionRepository;
import com.example.task330.discussion.repository.ReactionByTopicRepository;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

// Сервисный слой содержит бизнес-логику работы с реакциями.
@Service
public class ReactionService {

    private final ReactionRepository reactionRepository;
    private final ReactionByTopicRepository reactionByTopicRepository;

    // Spring передаёт Cassandra-репозитории через конструктор.
    public ReactionService(
            ReactionRepository reactionRepository,
            ReactionByTopicRepository reactionByTopicRepository) {
        this.reactionRepository = reactionRepository;
        this.reactionByTopicRepository = reactionByTopicRepository;
    }

    // Получение всех реакций.
    public List<ReactionResponseTo> findAll() {
        List<ReactionResponseTo> result = new ArrayList<>();
        reactionRepository.findAll().forEach(
                reaction -> result.add(toResponse(reaction))
        );
        return result;
    }

    // Получение реакции по идентификатору.
    public Optional<ReactionResponseTo> findById(Long id) {
        return reactionRepository.findById(id).map(this::toResponse);
    }

    // Получение всех реакций определённой темы.
    public List<ReactionResponseTo> findByTopicId(Long topicId) {
        return reactionByTopicRepository.findByKeyTopicId(topicId)
                .stream()
                .map(this::toResponse)
                .toList();
    }

    // Создание реакции через REST с генерацией идентификатора.
    public ReactionResponseTo save(ReactionRequestTo request) {
        return save(request, generateId(), "APPROVE");
    }

    // Сохранение реакции с заданным идентификатором и состоянием.
    public ReactionResponseTo save(
            ReactionRequestTo request,
            Long id,
            String state) {

        Reaction reaction = new Reaction(
                id,
                request.getTopicId(),
                request.getContent(),
                state
        );

        ReactionByTopic reactionByTopic = new ReactionByTopic(
                new ReactionByTopicKey(request.getTopicId(), id),
                request.getContent(),
                state
        );

        reactionRepository.save(reaction);
        reactionByTopicRepository.save(reactionByTopic);

        return toResponse(reaction);
    }

    // Обновление существующей реакции.
    public Optional<ReactionResponseTo> update(Long id, ReactionRequestTo request) {
        Optional<Reaction> existingReaction = reactionRepository.findById(id);
        if (existingReaction.isEmpty()) {
            return Optional.empty();
        }

        Reaction reaction = existingReaction.get();
        Long oldTopicId = reaction.getTopicId();
        String state = reaction.getState();

        reaction.setTopicId(request.getTopicId());
        reaction.setContent(request.getContent());

        reactionByTopicRepository.deleteById(
                new ReactionByTopicKey(oldTopicId, id)
        );

        reactionRepository.save(reaction);

        reactionByTopicRepository.save(
                new ReactionByTopic(
                        new ReactionByTopicKey(request.getTopicId(), id),
                        request.getContent(),
                        state
                )
        );

        return Optional.of(toResponse(reaction));
    }

    // Изменение состояния реакции после модерации.
    public Optional<ReactionResponseTo> updateState(Long id, String state) {
        Optional<Reaction> existingReaction = reactionRepository.findById(id);

        if (existingReaction.isEmpty()) {
            return Optional.empty();
        }

        Reaction reaction = existingReaction.get();
        reaction.setState(state);
        reactionRepository.save(reaction);

        ReactionByTopic reactionByTopic = reactionByTopicRepository
                .findById(new ReactionByTopicKey(reaction.getTopicId(), id))
                .orElse(null);

        if (reactionByTopic != null) {
            reactionByTopic.setState(state);
            reactionByTopicRepository.save(reactionByTopic);
        }

        return Optional.of(toResponse(reaction));
    }

    // Удаление реакции по идентификатору.
    public boolean deleteById(Long id) {
        Optional<Reaction> existingReaction = reactionRepository.findById(id);

        if (existingReaction.isEmpty()) {
            return false;
        }

        Long topicId = existingReaction.get().getTopicId();

        reactionRepository.deleteById(id);
        reactionByTopicRepository.deleteById(
                new ReactionByTopicKey(topicId, id)
        );

        return true;
    }

    // Удаление всех реакций определённой темы.
    public void deleteByTopicId(Long topicId) {
        List<ReactionByTopic> reactions =
                reactionByTopicRepository.findByKeyTopicId(topicId);

        for (ReactionByTopic reaction : reactions) {
            reactionRepository.deleteById(reaction.getKey().getId());
        }

        reactionByTopicRepository.deleteByKeyTopicId(topicId);
    }

    // Генерация положительного идентификатора реакции.
    private Long generateId() {
        return UUID.randomUUID().getMostSignificantBits() & Long.MAX_VALUE;
    }

    // Преобразование модели Reaction в DTO ответа.
    private ReactionResponseTo toResponse(Reaction reaction) {
        return new ReactionResponseTo(
                reaction.getId(),
                reaction.getTopicId(),
                reaction.getContent(),
                reaction.getState()
        );
    }

    // Преобразование модели ReactionByTopic в DTO ответа.
    private ReactionResponseTo toResponse(ReactionByTopic reaction) {
        return new ReactionResponseTo(
                reaction.getKey().getId(),
                reaction.getKey().getTopicId(),
                reaction.getContent(),
                reaction.getState()
        );
    }
}
