
package com.example.task310.controller;

import com.example.task310.dto.ReactionRequestTo;
import com.example.task310.dto.ReactionResponseTo;
import com.example.task310.exception.ResourceNotFoundException;
import com.example.task310.service.ReactionService;
import com.example.task310.service.TopicService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

// Контроллер обрабатывает REST-запросы для работы с реакциями.
// Базовый адрес запросов: /api/v1.0/reactions.
@RestController
@RequestMapping("/api/v1.0/reactions")
public class ReactionController {

    // Сервис выполняет операции создания, поиска, изменения и удаления реакций.
    private final ReactionService reactionService;

    // Сервис тем используется для проверки существования темы,
    // к которой относится реакция.
    private final TopicService topicService;

    // Получение необходимых сервисов через конструктор Spring.
    public ReactionController(
            ReactionService reactionService,
            TopicService topicService) {
        this.reactionService = reactionService;
        this.topicService = topicService;
    }

    // Получение списка всех реакций.
    // ReactionService получает данные из ReactionRepository
    // и преобразует модели Reaction в DTO ReactionResponseTo.
    // Результат возвращается клиенту с HTTP-кодом 200 OK.
    @GetMapping
    public ResponseEntity<List<ReactionResponseTo>> findAll() {
        return ResponseEntity.ok(reactionService.findAll());
    }

    // Получение реакции по идентификатору.
    // Сервис ищет реакцию и возвращает Optional с ReactionResponseTo.
    // Если реакция не найдена, выбрасывается ResourceNotFoundException,
    // которая обрабатывается GlobalExceptionHandler с кодом 404.
    @GetMapping("/{id}")
    public ResponseEntity<ReactionResponseTo> findById(@PathVariable Long id) {
        ReactionResponseTo reaction = reactionService.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Reaction with id " + id + " not found"
                ));

        return ResponseEntity.ok(reaction);
    }

    // Получение всех реакций, относящихся к определённой теме.
    // Сначала TopicService проверяет существование темы.
    // Если тема существует, ReactionService получает её реакции
    // через ReactionRepository по идентификатору темы.
    // Результат возвращается клиенту с HTTP-кодом 200 OK.
    @GetMapping("/topic/{topicId}")
    public ResponseEntity<List<ReactionResponseTo>> findByTopicId(
            @PathVariable Long topicId) {

        if (topicService.findById(topicId).isEmpty()) {
            throw new ResourceNotFoundException(
                    "Topic with id " + topicId + " not found"
            );
        }

        return ResponseEntity.ok(reactionService.findByTopicId(topicId));
    }

    // Создание новой реакции.
    // Полученные от клиента данные преобразуются в ReactionRequestTo.
    // @Valid проверяет ограничения, заданные в DTO.
    // ReactionService проверяет существование темы, создаёт модель Reaction,
    // сохраняет её через ReactionRepository и формирует ReactionResponseTo.
    // Если указанной темы нет, возвращается ошибка 404.
    // При успешном создании клиент получает HTTP-код 201 Created.
    @PostMapping
    public ResponseEntity<ReactionResponseTo> create(
            @Valid @RequestBody ReactionRequestTo request) {

        ReactionResponseTo reaction = reactionService.save(request)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Topic with id " + request.getTopicId() + " not found"
                ));

        return ResponseEntity.status(HttpStatus.CREATED).body(reaction);
    }

    // Обновление существующей реакции.
    // ReactionService находит реакцию, проверяет существование указанной темы,
    // обновляет данные и возвращает DTO ReactionResponseTo.
    // Если реакция или связанная с ней тема отсутствует,
    // выбрасывается ResourceNotFoundException.
    // Успешное изменение возвращается с HTTP-кодом 200 OK.
    @PutMapping("/{id}")
    public ResponseEntity<ReactionResponseTo> update(
            @PathVariable Long id,
            @Valid @RequestBody ReactionRequestTo request) {

        ReactionResponseTo reaction = reactionService.update(id, request)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Reaction or topic not found"
                ));

        return ResponseEntity.ok(reaction);
    }

    // Удаление реакции по идентификатору.
    // ReactionService обращается к ReactionRepository для удаления записи.
    // Если реакция не найдена, выбрасывается ResourceNotFoundException.
    // При успешном удалении возвращается HTTP-код 204 No Content.
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        if (!reactionService.deleteById(id)) {
            throw new ResourceNotFoundException(
                    "Reaction with id " + id + " not found"
            );
        }

        return ResponseEntity.noContent().build();
    }
}
