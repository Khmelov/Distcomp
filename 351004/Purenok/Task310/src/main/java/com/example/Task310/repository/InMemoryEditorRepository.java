package com.example.Task310.repository;

import com.example.Task310.bean.Editor;
import jakarta.annotation.PostConstruct;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;

@Repository
public class InMemoryEditorRepository implements CrudRepository<Editor, Long> {

    private final Map<Long, Editor> storage = new ConcurrentHashMap<>();
    private final AtomicLong idGenerator = new AtomicLong(1);

    // Создаем первую запись согласно заданию
    @PostConstruct
    public void init() {
        this.save(Editor.builder()
                .login("lkek31321@gmail.com")
                .password("supersecret123") // Пароль 8-128 символов
                .firstname("Александр")
                .lastname("Пуренок")
                .build());
    }

    @Override
    public Editor save(Editor entity) {
        Long id = idGenerator.getAndIncrement();
        entity.setId(id);
        storage.put(id, entity);
        return entity;
    }

    @Override
    public Optional<Editor> findById(Long id) {
        return Optional.ofNullable(storage.get(id));
    }

    @Override
    public List<Editor> findAll() {
        return new ArrayList<>(storage.values());
    }

    @Override
    public Editor update(Editor entity) {
        storage.put(entity.getId(), entity);
        return entity;
    }

    @Override
    public void deleteById(Long id) {
        storage.remove(id);
    }

    @Override
    public boolean existsById(Long id) {
        return storage.containsKey(id);
    }
}