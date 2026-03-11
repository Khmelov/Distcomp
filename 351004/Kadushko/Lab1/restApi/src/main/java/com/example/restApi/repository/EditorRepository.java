package com.example.restApi.repository;

import com.example.restApi.model.Editor;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public class EditorRepository extends InMemoryRepository<Editor> {

    public Optional<Editor> findByLogin(String login) {
        return storage.values().stream()
                .filter(e -> e.getLogin().equals(login))
                .findFirst();
    }

    public boolean existsByLogin(String login) {
        return storage.values().stream()
                .anyMatch(e -> e.getLogin().equals(login));
    }

    public boolean existsByLoginAndIdNot(String login, Long id) {
        return storage.values().stream()
                .anyMatch(e -> e.getLogin().equals(login) && !e.getId().equals(id));
    }
}
