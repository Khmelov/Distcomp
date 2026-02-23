package com.example.demo.repository;

import com.example.demo.model.Mark;
import org.springframework.stereotype.Repository;

@Repository
public class MarkRepository extends InMemoryRepository<Mark, Long>{

    @Override
    protected Long generatedId() {
        return id++;
    }

    @Override
    protected Long getId(Mark entity) {
        return entity.getId();
    }

    @Override
    protected void setId(Mark entity, Long id) {
        entity.setId(id);
    }
}
