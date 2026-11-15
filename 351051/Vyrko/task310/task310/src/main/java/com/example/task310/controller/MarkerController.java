
package com.example.task310.controller;

import com.example.task310.dto.MarkerRequestTo;
import com.example.task310.dto.MarkerResponseTo;
import com.example.task310.exception.ResourceNotFoundException;
import com.example.task310.repository.TopicMarkerRepository;
import com.example.task310.service.MarkerService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

// Контроллер обрабатывает REST-запросы для работы с маркерами.
// Базовый адрес запросов: /api/v1.0/markers.
@RestController
@RequestMapping("/api/v1.0/markers")
public class MarkerController {

    // Сервис выполняет основные операции создания, поиска и изменения маркеров.
    private final MarkerService markerService;

    // Репозиторий хранит связи между темами и маркерами (отношение M:N).
    private final TopicMarkerRepository topicMarkerRepository;

    // Получение зависимостей через конструктор Spring.
    public MarkerController(
            MarkerService markerService,
            TopicMarkerRepository topicMarkerRepository) {
        this.markerService = markerService;
        this.topicMarkerRepository = topicMarkerRepository;
    }

    // Получение списка всех маркеров.
    // MarkerService извлекает данные из MarkerRepository
    // и преобразует объекты Marker в DTO MarkerResponseTo.
    // Клиент получает список с HTTP-кодом 200 OK.
    @GetMapping
    public ResponseEntity<List<MarkerResponseTo>> findAll() {
        return ResponseEntity.ok(markerService.findAll());
    }

    // Получение маркера по идентификатору.
    // Сервис выполняет поиск и возвращает Optional с MarkerResponseTo.
    // Если маркер отсутствует, выбрасывается ResourceNotFoundException,
    // которая обрабатывается в GlobalExceptionHandler с кодом 404.
    @GetMapping("/{id}")
    public ResponseEntity<MarkerResponseTo> findById(@PathVariable Long id) {
        MarkerResponseTo marker = markerService.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Marker with id " + id + " not found"
                ));

        return ResponseEntity.ok(marker);
    }

    // Создание нового маркера.
    // JSON из запроса преобразуется в MarkerRequestTo.
    // Аннотация @Valid проверяет ограничения, заданные в DTO.
    // MarkerService создаёт модель Marker, сохраняет её через MarkerRepository
    // и возвращает результат в виде MarkerResponseTo.
    // HTTP-код 201 Created сообщает об успешном создании записи.
    @PostMapping
    public ResponseEntity<MarkerResponseTo> create(
            @Valid @RequestBody MarkerRequestTo request) {

        MarkerResponseTo createdMarker = markerService.save(request);

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(createdMarker);
    }

    // Изменение существующего маркера.
    // MarkerService ищет маркер по идентификатору и обновляет его название.
    // Если маркер не найден, выбрасывается ResourceNotFoundException.
    // Обновлённые данные возвращаются клиенту с HTTP-кодом 200 OK.
    @PutMapping("/{id}")
    public ResponseEntity<MarkerResponseTo> update(
            @PathVariable Long id,
            @Valid @RequestBody MarkerRequestTo request) {

        MarkerResponseTo marker = markerService.update(id, request)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Marker with id " + id + " not found"
                ));

        return ResponseEntity.ok(marker);
    }

    // Удаление маркера по идентификатору.
    // Сначала MarkerService удаляет маркер через MarkerRepository.
    // Если маркер не найден, выбрасывается ResourceNotFoundException.
    // Затем TopicMarkerRepository удаляет все связи этого маркера с темами,
    // чтобы в хранилище не оставались ссылки на удалённую запись.
    // При успешном удалении возвращается HTTP-код 204 No Content.
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        if (!markerService.deleteById(id)) {
            throw new ResourceNotFoundException(
                    "Marker with id " + id + " not found"
            );
        }

        topicMarkerRepository.deleteByMarkerId(id);
        return ResponseEntity.noContent().build();
    }
}
