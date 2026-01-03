package com.task.rest.repository;

import com.task.rest.model.Mark;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public class MarkRepository extends InMemoryRepository<Mark> {

    @Override
    protected Long getId(Mark entity) {
        return entity.getId();
    }

    @Override
    protected void setId(Mark entity, Long id) {
        entity.setId(id);
    }

    public Optional<Mark> findByName(String name) {
        return storage.values().stream()
                .filter(mark -> mark.getName().equals(name))
                .findFirst();
    }
}