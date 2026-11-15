
package com.example.task320.service;

import com.example.task320.dto.EditorRequestTo;
import com.example.task320.dto.EditorResponseTo;
import com.example.task320.entity.Editor;
import com.example.task320.entity.Marker;
import com.example.task320.entity.Topic;
import com.example.task320.exception.DuplicateLoginException;
import com.example.task320.repository.EditorRepository;
import com.example.task320.repository.MarkerRepository;
import com.example.task320.repository.TopicRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

// Сервисный слой содержит бизнес-логику работы с редакторами.
@Service
public class EditorService implements BaseService<Editor, Long, EditorResponseTo> {

    private final EditorRepository editorRepository;
    private final TopicRepository topicRepository;
    private final MarkerRepository markerRepository;

    // Spring передаёт необходимые репозитории через конструктор.
    public EditorService(
            EditorRepository editorRepository,
            TopicRepository topicRepository,
            MarkerRepository markerRepository) {
        this.editorRepository = editorRepository;
        this.topicRepository = topicRepository;
        this.markerRepository = markerRepository;
    }

    // Получение списка всех редакторов без пагинации.
    public List<EditorResponseTo> findAll() {
        return editorRepository.findAll()
                .stream()
                .map(this::toResponse)
                .toList();
    }

    // Получение редакторов с учётом пагинации.
    @Override
    public Page<EditorResponseTo> findAll(Pageable pageable) {
        return editorRepository.findAll(pageable)
                .map(this::toResponse);
    }

    // Получение редакторов с учётом сортировки.
    @Override
    public List<EditorResponseTo> findAll(Sort sort) {
        return editorRepository.findAll(sort)
                .stream()
                .map(this::toResponse)
                .toList();
    }

    // Получение редакторов по условиям фильтрации с пагинацией.
    @Override
    public Page<EditorResponseTo> findAll(
            Specification<Editor> specification,
            Pageable pageable) {
        return editorRepository.findAll(specification, pageable)
                .map(this::toResponse);
    }

    // Поиск редактора по идентификатору.
    @Override
    public Optional<EditorResponseTo> findById(Long id) {
        return editorRepository.findById(id)
                .map(this::toResponse);
    }

    // Создание нового редактора.
    public EditorResponseTo save(EditorRequestTo request) {
        checkLogin(request.getLogin(), null);

        Editor editor = new Editor();
        editor.setLogin(request.getLogin());
        editor.setPassword(request.getPassword());
        editor.setFirstname(request.getFirstname());
        editor.setLastname(request.getLastname());

        Editor savedEditor = editorRepository.save(editor);

        return toResponse(savedEditor);
    }

    // Обновление существующего редактора.
    public EditorResponseTo update(Long id, EditorRequestTo request) {
        Editor editor = editorRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException(
                        "Editor with id '" + id + "' not found"
                ));

        checkLogin(request.getLogin(), id);

        editor.setLogin(request.getLogin());
        editor.setPassword(request.getPassword());
        editor.setFirstname(request.getFirstname());
        editor.setLastname(request.getLastname());

        Editor updatedEditor = editorRepository.save(editor);

        return toResponse(updatedEditor);
    }

    // Проверка уникальности логина редактора.
    private void checkLogin(String login, Long currentEditorId) {
        Optional<Editor> existingEditor = editorRepository.findByLogin(login);

        if (existingEditor.isPresent()
                && !existingEditor.get().getId().equals(currentEditorId)) {
            throw new DuplicateLoginException(
                    "Editor with login '" + login + "' already exists"
            );
        }
    }

    // Удаление редактора и очистка неиспользуемых маркеров.
    @Transactional
    public boolean deleteById(Long id) {
        Optional<Editor> existingEditor = editorRepository.findById(id);

        if (existingEditor.isEmpty()) {
            return false;
        }

        // Получаем маркеры тем редактора до их удаления.
        List<Topic> editorTopics = topicRepository.findByEditor_Id(id);

        List<Long> markerIds = editorTopics.stream()
                .filter(topic -> topic.getMarkers() != null)
                .flatMap(topic -> topic.getMarkers().stream())
                .map(Marker::getId)
                .distinct()
                .toList();

        // Удаляем редактора вместе с его темами.
        editorRepository.delete(existingEditor.get());
        editorRepository.flush();

        // Удаляем маркеры, которые больше не используются другими темами.
        for (Long markerId : markerIds) {
            if (!topicRepository.existsByMarkers_Id(markerId)) {
                markerRepository.deleteById(markerId);
            }
        }

        return true;
    }

    // Преобразование сущности Editor в DTO ответа.
    private EditorResponseTo toResponse(Editor editor) {
        return new EditorResponseTo(
                editor.getId(),
                editor.getLogin(),
                editor.getFirstname(),
                editor.getLastname()
        );
    }
}
