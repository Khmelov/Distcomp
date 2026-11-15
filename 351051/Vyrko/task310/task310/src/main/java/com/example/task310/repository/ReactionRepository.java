
package com.example.task310.repository;

import com.example.task310.model.Reaction;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

// Репозиторий отвечает за доступ к данным реакций.
// Реализует общий интерфейс InMemoryRepository для модели Reaction.
// Используется ReactionService для выполнения операций с реакциями.
@Repository
public class ReactionRepository implements InMemoryRepository<Reaction> {

    // Универсальное хранилище реакций.
    // Reaction::getId и Reaction::setId передают методы получения
    // и установки идентификатора модели Reaction.
    private final InMemoryRepositoryImpl<Reaction> repository =
            new InMemoryRepositoryImpl<>(Reaction::getId, Reaction::setId);

    // Получение списка всех реакций.
    @Override
    public List<Reaction> findAll() {
        return repository.findAll();
    }

    // Поиск реакции по идентификатору.
    @Override
    public Optional<Reaction> findById(Long id) {
        return repository.findById(id);
    }

    // Получение списка реакций определённой темы.
    // Сначала извлекаются все реакции из хранилища.
    // Затем выбираются только те, у которых topicId совпадает
    // с идентификатором переданной темы.
    public List<Reaction> findByTopicId(Long topicId) {
        return repository.findAll().stream()
                .filter(reaction -> reaction.getTopicId().equals(topicId))
                .toList();
    }

    // Сохранение новой или обновлённой реакции.
    @Override
    public Reaction save(Reaction reaction) {
        return repository.save(reaction);
    }

    // Удаление реакции по идентификатору.
    @Override
    public boolean deleteById(Long id) {
        return repository.deleteById(id);
    }

    // Проверка существования реакции по идентификатору.
    @Override
    public boolean existsById(Long id) {
        return repository.existsById(id);
    }
}
