
package com.example.task310.repository;

import com.example.task310.model.Topic;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

// Репозиторий отвечает за доступ к данным тем.
// Реализует общий интерфейс InMemoryRepository для модели Topic.
// Используется TopicService для выполнения операций с темами.
@Repository
public class TopicRepository implements InMemoryRepository<Topic> {

    // Универсальное хранилище тем.
    // Topic::getId и Topic::setId передают методы получения
    // и установки идентификатора модели Topic.
    private final InMemoryRepositoryImpl<Topic> repository =
            new InMemoryRepositoryImpl<>(Topic::getId, Topic::setId);

    // Получение списка всех тем.
    @Override
    public List<Topic> findAll() {
        return repository.findAll();
    }

    // Поиск темы по идентификатору.
    @Override
    public Optional<Topic> findById(Long id) {
        return repository.findById(id);
    }

    // Получение всех тем конкретного редактора.
    // Сначала извлекаются все темы из хранилища.
    // Затем выбираются объекты, у которых editorId совпадает
    // с идентификатором переданного редактора.
    public List<Topic> findByEditorId(Long editorId) {
        return repository.findAll().stream()
                .filter(topic -> topic.getEditorId().equals(editorId))
                .toList();
    }

    // Сохранение новой или обновлённой темы.
    @Override
    public Topic save(Topic topic) {
        return repository.save(topic);
    }

    // Удаление темы по идентификатору.
    @Override
    public boolean deleteById(Long id) {
        return repository.deleteById(id);
    }

    // Проверка существования темы по идентификатору.
    @Override
    public boolean existsById(Long id) {
        return repository.existsById(id);
    }
}
