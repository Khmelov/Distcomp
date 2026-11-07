
package com.example.task310.service;

import com.example.task310.dto.TopicRequestTo;
import com.example.task310.dto.TopicResponseTo;
import com.example.task310.model.Topic;
import com.example.task310.repository.TopicRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

// Сервисный слой содержит бизнес-логику работы с темами.
// Получает запросы от TopicController, проверяет связанные сущности,
// обращается к TopicRepository и преобразует модели в DTO.
@Service
public class TopicService {

    // Репозиторий выполняет хранение, поиск и удаление тем.
    private final TopicRepository topicRepository;

    // Сервис редакторов используется для проверки существования автора темы.
    private final EditorService editorService;

    // Spring передаёт необходимые сервисы и репозитории через конструктор.
    public TopicService(
            TopicRepository topicRepository,
            EditorService editorService) {
        this.topicRepository = topicRepository;
        this.editorService = editorService;
    }

    // Получение списка всех тем.
    // Repository возвращает модели Topic, которые преобразуются в TopicResponseTo.
    // В результате контроллер получает список DTO для формирования HTTP-ответа.
    public List<TopicResponseTo> findAll() {
        return topicRepository.findAll()
                .stream()
                .map(this::toResponse)
                .toList();
    }

    // Поиск темы по идентификатору.
    // Если тема найдена, модель преобразуется в DTO ответа.
    // Если запись отсутствует, возвращается Optional.empty().
    public Optional<TopicResponseTo> findById(Long id) {
        return topicRepository.findById(id)
                .map(this::toResponse);
    }

    // Получение всех тем определённого редактора.
    // Repository находит темы по editorId.
    // Каждая найденная модель преобразуется в TopicResponseTo.
    public List<TopicResponseTo> findByEditorId(Long editorId) {
        return topicRepository.findByEditorId(editorId)
                .stream()
                .map(this::toResponse)
                .toList();
    }

    // Создание новой темы.
    // Сначала проверяется существование редактора, указанного в запросе.
    // Если редактор не найден, возвращается Optional.empty().
    // Для новой темы устанавливаются одинаковые даты создания и изменения.
    // Затем создаётся модель Topic и заполняется данными из TopicRequestTo.
    // Repository сохраняет модель и возвращает сохранённую запись.
    // Результат преобразуется в TopicResponseTo.
    public Optional<TopicResponseTo> save(TopicRequestTo request) {
        if (editorService.findById(request.getEditorId()).isEmpty()) {
            return Optional.empty();
        }

        LocalDateTime now = LocalDateTime.now();

        Topic topic = new Topic();
        topic.setEditorId(request.getEditorId());
        topic.setTitle(request.getTitle());
        topic.setContent(request.getContent());
        topic.setCreated(now);
        topic.setModified(now);

        Topic savedTopic = topicRepository.save(topic);

        return Optional.of(toResponse(savedTopic));
    }

    // Обновление существующей темы.
    // Проверяется наличие темы и редактора, указанного в запросе.
    // Если хотя бы одна проверка не пройдена, возвращается Optional.empty().
    // При обновлении изменяются автор, заголовок и содержание темы.
    // Дата создания сохраняется, а дата последнего изменения обновляется.
    // Repository сохраняет изменения, после чего модель преобразуется в DTO.
    public Optional<TopicResponseTo> update(Long id, TopicRequestTo request) {
        Optional<Topic> existingTopic = topicRepository.findById(id);

        if (existingTopic.isEmpty()
                || editorService.findById(request.getEditorId()).isEmpty()) {
            return Optional.empty();
        }

        Topic topic = existingTopic.get();
        topic.setEditorId(request.getEditorId());
        topic.setTitle(request.getTitle());
        topic.setContent(request.getContent());
        topic.setModified(LocalDateTime.now());

        Topic updatedTopic = topicRepository.save(topic);

        return Optional.of(toResponse(updatedTopic));
    }

    // Удаление темы по идентификатору.
    // Repository удаляет запись из хранилища и возвращает результат операции.
    public boolean deleteById(Long id) {
        return topicRepository.deleteById(id);
    }

    // Преобразование модели Topic в DTO ответа.
    // В DTO передаются идентификаторы темы и редактора,
    // заголовок, содержание, дата создания и дата изменения.
    // Метод используется при получении, создании и обновлении тем.
    private TopicResponseTo toResponse(Topic topic) {
        return new TopicResponseTo(
                topic.getId(),
                topic.getEditorId(),
                topic.getTitle(),
                topic.getContent(),
                topic.getCreated(),
                topic.getModified()
        );
    }
}
