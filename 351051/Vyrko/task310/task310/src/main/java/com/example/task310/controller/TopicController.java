
package com.example.task310.controller;

import com.example.task310.dto.MarkerResponseTo;
import com.example.task310.dto.TopicRequestTo;
import com.example.task310.dto.TopicResponseTo;
import com.example.task310.exception.ResourceNotFoundException;
import com.example.task310.repository.TopicMarkerRepository;
import com.example.task310.service.EditorService;
import com.example.task310.service.MarkerService;
import com.example.task310.service.TopicService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

// Контроллер обрабатывает REST-запросы для работы с темами.
// Дополнительно обеспечивает управление связями тем с маркерами.
// Базовый адрес запросов: /api/v1.0/topics.
@RestController
@RequestMapping("/api/v1.0/topics")
public class TopicController {

    // Сервис выполняет основные операции с темами.
    private final TopicService topicService;

    // Сервис редакторов используется для проверки автора темы.
    private final EditorService editorService;

    // Сервис маркеров используется для поиска связанных маркеров.
    private final MarkerService markerService;

    // Репозиторий хранит связи между темами и маркерами (отношение M:N).
    private final TopicMarkerRepository topicMarkerRepository;

    // Получение необходимых зависимостей через конструктор Spring.
    public TopicController(
            TopicService topicService,
            EditorService editorService,
            MarkerService markerService,
            TopicMarkerRepository topicMarkerRepository) {
        this.topicService = topicService;
        this.editorService = editorService;
        this.markerService = markerService;
        this.topicMarkerRepository = topicMarkerRepository;
    }

    // Получение списка всех тем.
    // TopicService получает модели Topic из TopicRepository
    // и преобразует их в DTO TopicResponseTo.
    // Список возвращается клиенту с HTTP-кодом 200 OK.
    @GetMapping
    public ResponseEntity<List<TopicResponseTo>> findAll() {
        return ResponseEntity.ok(topicService.findAll());
    }

    // Получение темы по идентификатору.
    // TopicService выполняет поиск и возвращает Optional с TopicResponseTo.
    // Если тема отсутствует, выбрасывается ResourceNotFoundException,
    // которая обрабатывается GlobalExceptionHandler с кодом 404.
    @GetMapping("/{id}")
    public ResponseEntity<TopicResponseTo> findById(@PathVariable Long id) {
        TopicResponseTo topic = topicService.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Topic with id " + id + " not found"
                ));

        return ResponseEntity.ok(topic);
    }

    // Получение всех тем конкретного редактора.
    // Сначала EditorService проверяет существование редактора.
    // Затем TopicService получает список его тем через TopicRepository.
    // Результат возвращается клиенту с HTTP-кодом 200 OK.
    @GetMapping("/editor/{editorId}")
    public ResponseEntity<List<TopicResponseTo>> findByEditorId(
            @PathVariable Long editorId) {

        if (editorService.findById(editorId).isEmpty()) {
            throw new ResourceNotFoundException(
                    "Editor with id " + editorId + " not found"
            );
        }

        return ResponseEntity.ok(topicService.findByEditorId(editorId));
    }

    // Создание новой темы.
    // JSON из запроса преобразуется в TopicRequestTo.
    // @Valid проверяет обязательные поля и ограничения DTO.
    // TopicService проверяет существование автора, создаёт модель Topic,
    // устанавливает время создания и изменения, сохраняет запись
    // через TopicRepository и возвращает TopicResponseTo.
    // Если редактор не найден, возвращается ошибка 404.
    // При успешном создании возвращается HTTP-код 201 Created.
    @PostMapping
    public ResponseEntity<TopicResponseTo> create(
            @Valid @RequestBody TopicRequestTo request) {

        TopicResponseTo topic = topicService.save(request)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Editor with id " + request.getEditorId() + " not found"
                ));

        return ResponseEntity.status(HttpStatus.CREATED).body(topic);
    }

    // Обновление существующей темы.
    // TopicService находит тему, проверяет существование нового автора,
    // изменяет данные и обновляет время последнего изменения.
    // Если тема или редактор отсутствуют, возвращается ошибка 404.
    // Обновлённая тема возвращается в виде TopicResponseTo с кодом 200 OK.
    @PutMapping("/{id}")
    public ResponseEntity<TopicResponseTo> update(
            @PathVariable Long id,
            @Valid @RequestBody TopicRequestTo request) {

        TopicResponseTo topic = topicService.update(id, request)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Topic or editor not found"
                ));

        return ResponseEntity.ok(topic);
    }

    // Удаление темы по идентификатору.
    // Сначала TopicService удаляет тему через TopicRepository.
    // Если тема отсутствует, выбрасывается ResourceNotFoundException.
    // Затем TopicMarkerRepository удаляет все связи удалённой темы с маркерами,
    // чтобы в хранилище не оставались ссылки на несуществующую тему.
    // При успешном удалении возвращается HTTP-код 204 No Content.
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        if (!topicService.deleteById(id)) {
            throw new ResourceNotFoundException(
                    "Topic with id " + id + " not found"
            );
        }

        topicMarkerRepository.deleteByTopicId(id);
        return ResponseEntity.noContent().build();
    }

    // Получение списка маркеров, связанных с определённой темой.
    // Сначала проверяется существование темы через TopicService.
    // Затем TopicMarkerRepository получает идентификаторы связанных маркеров.
    // Для каждого идентификатора MarkerService выполняет поиск маркера
    // и возвращает Optional с DTO MarkerResponseTo.
    // Optional::stream исключает отсутствующие записи из итогового списка.
    // Клиент получает список маркеров с HTTP-кодом 200 OK.
    @GetMapping("/{id}/markers")
    public ResponseEntity<List<MarkerResponseTo>> findMarkers(@PathVariable Long id) {
        if (topicService.findById(id).isEmpty()) {
            throw new ResourceNotFoundException(
                    "Topic with id " + id + " not found"
            );
        }

        List<MarkerResponseTo> markers = topicMarkerRepository
                .findMarkerIdsByTopicId(id)
                .stream()
                .map(markerService::findById)
                .flatMap(Optional::stream)
                .toList();

        return ResponseEntity.ok(markers);
    }

    // Добавление маркера к теме.
    // Сначала TopicService и MarkerService проверяют существование обеих записей.
    // Затем TopicMarkerRepository создаёт связь между их идентификаторами.
    // Таким образом реализуется отношение многие-ко-многим (M:N).
    // При успешном добавлении связи возвращается HTTP-код 200 OK.
    @PutMapping("/{id}/markers/{markerId}")
    public ResponseEntity<Void> addMarker(
            @PathVariable Long id,
            @PathVariable Long markerId) {

        if (topicService.findById(id).isEmpty()) {
            throw new ResourceNotFoundException(
                    "Topic with id " + id + " not found"
            );
        }

        if (markerService.findById(markerId).isEmpty()) {
            throw new ResourceNotFoundException(
                    "Marker with id " + markerId + " not found"
            );
        }

        topicMarkerRepository.addMarker(id, markerId);
        return ResponseEntity.ok().build();
    }

    // Удаление связи между темой и маркером.
    // Перед удалением проверяется существование темы и маркера.
    // TopicMarkerRepository удаляет связь по двум идентификаторам.
    // Сами записи темы и маркера при этом остаются в хранилищах.
    // При успешном удалении связи возвращается HTTP-код 204 No Content.
    @DeleteMapping("/{id}/markers/{markerId}")
    public ResponseEntity<Void> removeMarker(
            @PathVariable Long id,
            @PathVariable Long markerId) {

        if (topicService.findById(id).isEmpty()) {
            throw new ResourceNotFoundException(
                    "Topic with id " + id + " not found"
            );
        }

        if (markerService.findById(markerId).isEmpty()) {
            throw new ResourceNotFoundException(
                    "Marker with id " + markerId + " not found"
            );
        }

        topicMarkerRepository.removeMarker(id, markerId);
        return ResponseEntity.noContent().build();
    }
}
