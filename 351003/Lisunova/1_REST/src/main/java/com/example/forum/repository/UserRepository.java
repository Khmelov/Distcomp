package com.example.forum.repository;

import com.example.forum.entity.User;
import org.springframework.stereotype.Repository;

@Repository
public class UserRepository extends InMemoryCrudRepository<User> {

    public UserRepository() {
        super(new IdAccessor<>() {
            @Override
            public Long getId(User entity) {
                return entity.getId();
            }

            @Override
            public void setId(User entity, Long id) {
                entity.setId(id);
            }
        });
    }
}
