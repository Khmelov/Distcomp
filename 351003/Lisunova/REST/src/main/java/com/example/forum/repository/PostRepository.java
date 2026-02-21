package com.example.forum.repository;

import com.example.forum.entity.Post;
import org.springframework.stereotype.Repository;

@Repository
public class PostRepository extends InMemoryCrudRepository<Post> {

    public PostRepository() {
        super(new IdAccessor<>() {
            @Override
            public Long getId(Post entity) {
                return entity.getId();
            }

            @Override
            public void setId(Post entity, Long id) {
                entity.setId(id);
            }
        });
    }
}
