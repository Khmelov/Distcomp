
package com.example.task310.service;

import com.example.task310.dto.ReactionRequestTo;
import com.example.task310.dto.ReactionResponseTo;
import com.example.task310.model.Reaction;
import com.example.task310.repository.ReactionRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

// Сервисный слой содержит бизнес-логику работы с реакциями.
// Получает запросы от ReactionController, проверяет существование темы,
// взаимодействует с ReactionRepository и преобразует модели в DTO.
@Service
public class ReactionService {

    // Репозиторий отвечает за хранение, поиск и удаление реакций.
    private final ReactionRepository reactionRepository;

    // Сервис тем используется для проверки существования темы,
    // к которой относится создаваемая или обновляемая реакция.
    private final TopicService topicService;

    // Spring передаёт необходимые зависимости через конструктор.
    public ReactionService(
            ReactionRepository reactionRepository,
            TopicService topicService) {
        this.reactionRepository = reactionRepository;
        this.topicService = topicService;
    }

    // Получение списка всех реакций.
    // Repository возвращает модели Reaction.
    // Каждая модель преобразуется в ReactionResponseTo.
    public List<ReactionResponseTo> findAll() {
        return reactionRepository.findAll()
                .stream()
                .map(this::toResponse)
                .toList();
    }

    // Поиск реакции по идентификатору.
    // Если запись найдена, модель преобразуется в DTO ответа.
    // Если реакция отсутствует, возвращается Optional.empty().
    public Optional<ReactionResponseTo> findById(Long id) {
        return reactionRepository.findById(id)
                .map(this::toResponse);
    }

    // Получение всех реакций определённой темы.
    // Repository находит реакции по идентификатору темы.
    // Каждая найденная модель преобразуется в ReactionResponseTo.
    public List<ReactionResponseTo> findByTopicId(Long topicId) {
        return reactionRepository.findByTopicId(topicId)
                .stream()
                .map(this::toResponse)
                .toList();
    }

    // Создание новой реакции.
    // Сначала через TopicService проверяется существование темы.
    // Если тема не найдена, возвращается Optional.empty().
    // Если тема существует, создаётся модель Reaction
    // и заполняется идентификатором темы и содержанием реакции.
    // Repository сохраняет модель и возвращает её с идентификатором.
    // Сохранённая модель преобразуется в DTO ответа.
    public Optional<ReactionResponseTo> save(ReactionRequestTo request) {
        if (topicService.findById(request.getTopicId()).isEmpty()) {
            return Optional.empty();
        }

        Reaction reaction = new Reaction();
        reaction.setTopicId(request.getTopicId());
        reaction.setContent(request.getContent());

        Reaction savedReaction = reactionRepository.save(reaction);

        return Optional.of(toResponse(savedReaction));
    }

    // Обновление существующей реакции.
    // Проверяется наличие самой реакции и темы, к которой она относится.
    // Если хотя бы одна проверка не пройдена, возвращается Optional.empty().
    // Если обе проверки успешны, изменяются идентификатор темы и содержание.
    // Repository сохраняет обновлённую модель.
    // Результат преобразуется в ReactionResponseTo.
    public Optional<ReactionResponseTo> update(Long id, ReactionRequestTo request) {
        Optional<Reaction> existingReaction = reactionRepository.findById(id);

        if (existingReaction.isEmpty()
                || topicService.findById(request.getTopicId()).isEmpty()) {
            return Optional.empty();
        }

        Reaction reaction = existingReaction.get();
        reaction.setTopicId(request.getTopicId());
        reaction.setContent(request.getContent());

        Reaction updatedReaction = reactionRepository.save(reaction);

        return Optional.of(toResponse(updatedReaction));
    }

    // Удаление реакции по идентификатору.
    // Repository удаляет запись из хранилища и возвращает результат операции.
    public boolean deleteById(Long id) {
        return reactionRepository.deleteById(id);
    }

    // Преобразование модели Reaction в DTO ответа.
    // В DTO передаются идентификатор реакции, идентификатор темы
    // и содержание реакции.
    // Метод используется при получении, создании и обновлении реакций.
    private ReactionResponseTo toResponse(Reaction reaction) {
        return new ReactionResponseTo(
                reaction.getId(),
                reaction.getTopicId(),
                reaction.getContent()
        );
    }
}
