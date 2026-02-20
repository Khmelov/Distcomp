package com.example.demo.repository;

import com.example.demo.model.Message;
import org.springframework.stereotype.Repository;

@Repository
public class MessageRepository extends InMemoryRepository<Message, Long>{
    @Override
    protected Long generatedId() {
        return id++;
    }

    @Override
    protected Long getId(Message entity) {
        return entity.getId();
    }

    @Override
    protected void setId(Message entity, Long id) {
        entity.setId(id);
    }
}
