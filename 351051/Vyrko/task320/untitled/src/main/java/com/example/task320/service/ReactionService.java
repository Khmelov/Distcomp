package com.example.task320.service;

import com.example.task320.dto.ReactionRequestTo;
import com.example.task320.dto.ReactionResponseTo;
import com.example.task320.entity.Reaction;
import com.example.task320.entity.Topic;
import com.example.task320.repository.ReactionRepository;
import com.example.task320.repository.TopicRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

// Сервисный слой содержит бизнес-логику работы с реакциями.
// Проверяет существование темы и преобразует сущности Reaction в DTO.
@Service
public class ReactionService implements BaseService<Reaction, Long, ReactionResponseTo> {

    // Репозиторий отвечает за хранение, поиск и удаление реакций.
    private final ReactionRepository reactionRepository;

    // Репозиторий тем используется для получения темы реакции.
    private final TopicRepository topicRepository;

    // Spring передаёт необходимые репозитории через конструктор.
    public ReactionService(
            ReactionRepository reactionRepository,
            TopicRepository topicRepository) {
        this.reactionRepository = reactionRepository;
        this.topicRepository = topicRepository;
    }

    // Получение списка всех реакций без пагинации.
    public List<ReactionResponseTo> findAll() {
        return reactionRepository.findAll()
                .stream()
                .map(this::toResponse)
                .toList();
    }

    // Получение реакций с учётом пагинации.
    @Override
    public Page<ReactionResponseTo> findAll(Pageable pageable) {
        return reactionRepository.findAll(pageable)
                .map(this::toResponse);
    }

    // Получение реакций с учётом сортировки.
    @Override
    public List<ReactionResponseTo> findAll(Sort sort) {
        return reactionRepository.findAll(sort)
                .stream()
                .map(this::toResponse)
                .toList();
    }

    // Получение реакций по условиям фильтрации с пагинацией.
    @Override
    public Page<ReactionResponseTo> findAll(
            Specification<Reaction> specification,
            Pageable pageable) {
        return reactionRepository.findAll(specification, pageable)
                .map(this::toResponse);
    }

    // Поиск реакции по идентификатору.
    @Override
    public Optional<ReactionResponseTo> findById(Long id) {
        return reactionRepository.findById(id)
                .map(this::toResponse);
    }

    // Получение всех реакций определённой темы.
    public List<ReactionResponseTo> findByTopicId(Long topicId) {
        return reactionRepository.findByTopic_Id(topicId)
                .stream()
                .map(this::toResponse)
                .toList();
    }

    // Создание новой реакции.
    // Сначала проверяется существование темы, указанной в запросе.
    public Optional<ReactionResponseTo> save(ReactionRequestTo request) {
        Optional<Topic> existingTopic =
                topicRepository.findById(request.getTopicId());

        if (existingTopic.isEmpty()) {
            return Optional.empty();
        }

        Reaction reaction = new Reaction();
        reaction.setTopic(existingTopic.get());
        reaction.setContent(request.getContent());

        Reaction savedReaction = reactionRepository.save(reaction);

        return Optional.of(toResponse(savedReaction));
    }

    // Обновление существующей реакции.
    // Проверяется наличие реакции и темы, к которой она относится.
    public Optional<ReactionResponseTo> update(Long id, ReactionRequestTo request) {
        Optional<Reaction> existingReaction = reactionRepository.findById(id);

        if (existingReaction.isEmpty()) {
            return Optional.empty();
        }

        Optional<Topic> existingTopic =
                topicRepository.findById(request.getTopicId());

        if (existingTopic.isEmpty()) {
            return Optional.empty();
        }

        Reaction reaction = existingReaction.get();
        reaction.setTopic(existingTopic.get());
        reaction.setContent(request.getContent());

        Reaction updatedReaction = reactionRepository.save(reaction);

        return Optional.of(toResponse(updatedReaction));
    }

    // Удаление реакции по идентификатору.
    public boolean deleteById(Long id) {
        if (!reactionRepository.existsById(id)) {
            return false;
        }

        reactionRepository.deleteById(id);
        return true;
    }

    // Преобразование сущности Reaction в DTO ответа.
    // Вместо объекта Topic передаётся только его идентификатор.
    private ReactionResponseTo toResponse(Reaction reaction) {
        return new ReactionResponseTo(
                reaction.getId(),
                reaction.getTopic().getId(),
                reaction.getContent()
        );
    }
}