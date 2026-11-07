
package com.example.task310.repository;

import com.example.task310.model.Marker;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

// Репозиторий отвечает за доступ к данным маркеров.
// Реализует общий интерфейс InMemoryRepository для модели Marker.
// Используется MarkerService для выполнения операций с маркерами.
@Repository
public class MarkerRepository implements InMemoryRepository<Marker> {

    // Универсальное хранилище маркеров.
    // Marker::getId и Marker::setId передают методы получения
    // и установки идентификатора модели Marker.
    private final InMemoryRepositoryImpl<Marker> repository =
            new InMemoryRepositoryImpl<>(Marker::getId, Marker::setId);

    // Получение списка всех маркеров.
    @Override
    public List<Marker> findAll() {
        return repository.findAll();
    }

    // Поиск маркера по идентификатору.
    @Override
    public Optional<Marker> findById(Long id) {
        return repository.findById(id);
    }

    // Поиск маркера по названию.
    // Сравнение выполняется без учёта регистра букв.
    // Например, названия "Java" и "java" считаются совпадающими.
    public Optional<Marker> findByName(String name) {
        return repository.findAll().stream()
                .filter(marker -> marker.getName().equalsIgnoreCase(name))
                .findFirst();
    }

    // Сохранение нового или обновлённого маркера.
    @Override
    public Marker save(Marker marker) {
        return repository.save(marker);
    }

    // Удаление маркера по идентификатору.
    @Override
    public boolean deleteById(Long id) {
        return repository.deleteById(id);
    }

    // Проверка существования маркера по идентификатору.
    @Override
    public boolean existsById(Long id) {
        return repository.existsById(id);
    }
}
