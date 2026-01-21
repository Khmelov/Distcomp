package com.example.app.repository;

import org.springframework.stereotype.Repository;

import com.example.app.model.Author;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;

@Repository
public class InMemoryAuthorRepository implements CrudRepository<Author, Long> {
    private final Map<Long, Author> store = new ConcurrentHashMap<>();
    private final AtomicLong idGenerator = new AtomicLong(1);

    @Override
    public List<Author> findAll() {
        return new ArrayList<>(store.values());
    }

    @Override
    public Optional<Author> findById(Long id) {
        return Optional.ofNullable(store.get(id));
    }

    @Override
    public Author save(Author author) {
        if (author.getId() == null) {
            author.setId(idGenerator.getAndIncrement());
        }
        store.put(author.getId(), author);
        return author;
    }

    @Override
    public boolean deleteById(Long id) {
        return store.remove(id) != null;
    }

    @Override
    public long count() {
        return store.size();
    }

    public Author findByLogin(String login) {
        return store.values().stream()
                .filter(a -> a.getLogin().equals(login))
                .findFirst()
                .orElse(null);
    }
}