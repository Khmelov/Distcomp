package by.bsuir.distcomp.repository.impl;

import by.bsuir.distcomp.entity.Author;
import by.bsuir.distcomp.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.*;
import java.util.concurrent.atomic.AtomicLong;

@Repository
public class InMemoryAuthorRepository implements CrudRepository<Author, Long> {
    private final Map<Long, Author> storage = new HashMap<>();
    private final AtomicLong idGenerator = new AtomicLong(1);

    @Override
    public Author save(Author entity) {
        if (entity.getId() == null) {
            entity.setId(idGenerator.getAndIncrement());
        }
        storage.put(entity.getId(), entity);
        return entity;
    }

    @Override
    public Optional<Author> findById(Long id) {
        return Optional.ofNullable(storage.get(id));
    }

    @Override
    public List<Author> findAll() {
        return new ArrayList<>(storage.values());
    }

    @Override
    public Author update(Author entity) {
        if (entity.getId() == null || !storage.containsKey(entity.getId())) {
            throw new IllegalArgumentException("Entity with id " + entity.getId() + " does not exist");
        }
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

    public boolean existsByLogin(String login) {
        return storage.values().stream()
                .anyMatch(author -> login.equals(author.getLogin()));
    }

    public Optional<Author> findByLogin(String login) {
        return storage.values().stream()
                .filter(author -> login.equals(author.getLogin()))
                .findFirst();
    }
}
