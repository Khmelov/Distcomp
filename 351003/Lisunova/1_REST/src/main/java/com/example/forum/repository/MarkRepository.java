package com.example.forum.repository;

import com.example.forum.entity.Mark;
import org.springframework.stereotype.Repository;

@Repository
public class MarkRepository extends InMemoryCrudRepository<Mark> {

    public MarkRepository() {
        super(new IdAccessor<>() {
            @Override
            public Long getId(Mark entity) {
                return entity.getId();
            }

            @Override
            public void setId(Mark entity, Long id) {
                entity.setId(id);
            }
        });
    }
}
