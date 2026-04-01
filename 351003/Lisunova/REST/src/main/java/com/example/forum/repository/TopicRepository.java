package com.example.forum.repository;

import com.example.forum.entity.Topic;
import org.springframework.stereotype.Repository;

@Repository
public class TopicRepository extends InMemoryCrudRepository<Topic> {

    public TopicRepository() {
        super(new IdAccessor<>() {
            @Override
            public Long getId(Topic entity) {
                return entity.getId();
            }

            @Override
            public void setId(Topic entity, Long id) {
                entity.setId(id);
            }
        });
    }
}
