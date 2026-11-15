
package com.example.task320.service;

import com.example.task320.dto.MarkerResponseTo;
import com.example.task320.dto.TopicRequestTo;
import com.example.task320.dto.TopicResponseTo;
import com.example.task320.entity.Editor;
import com.example.task320.entity.Marker;
import com.example.task320.entity.Topic;
import com.example.task320.exception.DuplicateTopicException;
import com.example.task320.repository.EditorRepository;
import com.example.task320.repository.MarkerRepository;
import com.example.task320.repository.TopicRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

// Сервисный слой содержит бизнес-логику работы с темами.
@Service
public class TopicService implements BaseService<Topic, Long, TopicResponseTo> {

    private final TopicRepository topicRepository;
    private final EditorRepository editorRepository;
    private final MarkerRepository markerRepository;

    // Spring передаёт необходимые репозитории через конструктор.
    public TopicService(
            TopicRepository topicRepository,
            EditorRepository editorRepository,
            MarkerRepository markerRepository) {
        this.topicRepository = topicRepository;
        this.editorRepository = editorRepository;
        this.markerRepository = markerRepository;
    }

    // Получение списка всех тем без пагинации.
    public List<TopicResponseTo> findAll() {
        return topicRepository.findAll()
                .stream()
                .map(this::toResponse)
                .toList();
    }

    // Получение тем с учётом пагинации.
    @Override
    public Page<TopicResponseTo> findAll(Pageable pageable) {
        return topicRepository.findAll(pageable)
                .map(this::toResponse);
    }

    // Получение тем с учётом сортировки.
    @Override
    public List<TopicResponseTo> findAll(Sort sort) {
        return topicRepository.findAll(sort)
                .stream()
                .map(this::toResponse)
                .toList();
    }

    // Получение тем по условиям фильтрации с пагинацией.
    @Override
    public Page<TopicResponseTo> findAll(
            Specification<Topic> specification,
            Pageable pageable) {
        return topicRepository.findAll(specification, pageable)
                .map(this::toResponse);
    }

    // Поиск темы по идентификатору.
    @Override
    public Optional<TopicResponseTo> findById(Long id) {
        return topicRepository.findById(id)
                .map(this::toResponse);
    }

    // Получение всех тем определённого редактора.
    public List<TopicResponseTo> findByEditorId(Long editorId) {
        return topicRepository.findByEditor_Id(editorId)
                .stream()
                .map(this::toResponse)
                .toList();
    }

    // Создание новой темы.
    public Optional<TopicResponseTo> save(TopicRequestTo request) {
        Optional<Editor> existingEditor =
                editorRepository.findById(request.getEditorId());

        if (existingEditor.isEmpty()) {
            return Optional.empty();
        }

        // Проверка, что заголовок ещё не занят другой темой.
        if (topicRepository.findByTitle(request.getTitle()).isPresent()) {
            throw new DuplicateTopicException(
                    "Topic with title '" + request.getTitle() + "' already exists"
            );
        }

        LocalDateTime now = LocalDateTime.now();

        Topic topic = new Topic();
        topic.setEditor(existingEditor.get());
        topic.setTitle(request.getTitle());
        topic.setContent(request.getContent());
        topic.setCreated(now);
        topic.setModified(now);

        // Поиск существующих маркеров и создание новых при необходимости.
        if (request.getMarkers() != null && !request.getMarkers().isEmpty()) {
            List<Marker> markers = request.getMarkers().stream()
                    .distinct()
                    .map(name -> markerRepository.findByName(name)
                            .orElseGet(() -> markerRepository.save(new Marker(name))))
                    .toList();

            topic.setMarkers(markers);
        }

        Topic savedTopic = topicRepository.save(topic);

        return Optional.of(toResponse(savedTopic));
    }

    // Обновление существующей темы.
    public Optional<TopicResponseTo> update(Long id, TopicRequestTo request) {
        Optional<Topic> existingTopic = topicRepository.findById(id);

        if (existingTopic.isEmpty()) {
            return Optional.empty();
        }

        Optional<Editor> existingEditor =
                editorRepository.findById(request.getEditorId());

        if (existingEditor.isEmpty()) {
            return Optional.empty();
        }

        // Проверка, что заголовок не используется другой темой.
        Optional<Topic> topicWithTitle =
                topicRepository.findByTitle(request.getTitle());

        if (topicWithTitle.isPresent()
                && !topicWithTitle.get().getId().equals(id)) {
            throw new DuplicateTopicException(
                    "Topic with title '" + request.getTitle() + "' already exists"
            );
        }

        Topic topic = existingTopic.get();
        topic.setEditor(existingEditor.get());
        topic.setTitle(request.getTitle());
        topic.setContent(request.getContent());
        topic.setModified(LocalDateTime.now());

        Topic updatedTopic = topicRepository.save(topic);

        return Optional.of(toResponse(updatedTopic));
    }

    // Получение списка маркеров определённой темы.
    @Transactional(readOnly = true)
    public Optional<List<MarkerResponseTo>> findMarkers(Long topicId) {
        return topicRepository.findById(topicId)
                .map(topic -> topic.getMarkers()
                        .stream()
                        .map(marker -> new MarkerResponseTo(
                                marker.getId(),
                                marker.getName()
                        ))
                        .toList());
    }

    // Добавление существующего маркера к теме.
    @Transactional
    public boolean addMarker(Long topicId, Long markerId) {
        Optional<Topic> existingTopic = topicRepository.findById(topicId);
        Optional<Marker> existingMarker = markerRepository.findById(markerId);

        if (existingTopic.isEmpty() || existingMarker.isEmpty()) {
            return false;
        }

        Topic topic = existingTopic.get();
        Marker marker = existingMarker.get();

        if (topic.getMarkers() == null) {
            topic.setMarkers(new java.util.ArrayList<>());
        }

        if (topic.getMarkers().stream()
                .anyMatch(existing -> existing.getId().equals(markerId))) {
            return true;
        }

        topic.getMarkers().add(marker);
        topicRepository.save(topic);

        return true;
    }

    // Удаление маркера из темы.
    @Transactional
    public boolean removeMarker(Long topicId, Long markerId) {
        Optional<Topic> existingTopic = topicRepository.findById(topicId);

        if (existingTopic.isEmpty()) {
            return false;
        }

        Topic topic = existingTopic.get();

        if (topic.getMarkers() == null) {
            return false;
        }

        boolean removed = topic.getMarkers()
                .removeIf(marker -> marker.getId().equals(markerId));

        if (removed) {
            topicRepository.save(topic);
        }

        return removed;
    }

    // Удаление темы по идентификатору.
    @Transactional
    public boolean deleteById(Long id) {
        Optional<Topic> existingTopic = topicRepository.findById(id);

        if (existingTopic.isEmpty()) {
            return false;
        }

        // Сохраняем идентификаторы маркеров до удаления темы.
        List<Long> markerIds = existingTopic.get().getMarkers().stream()
                .map(Marker::getId)
                .toList();

        // Удаляем тему вместе со связями в таблице topic_marker.
        topicRepository.delete(existingTopic.get());
        topicRepository.flush();

        // Удаляем маркеры, которые больше не используются другими темами.
        for (Long markerId : markerIds) {
            if (!topicRepository.existsByMarkers_Id(markerId)) {
                markerRepository.deleteById(markerId);
            }
        }

        return true;
    }

    // Преобразование сущности Topic в DTO ответа.
    private TopicResponseTo toResponse(Topic topic) {
        return new TopicResponseTo(
                topic.getId(),
                topic.getEditor().getId(),
                topic.getTitle(),
                topic.getContent(),
                topic.getCreated(),
                topic.getModified()
        );
    }
}
