package com.publick.repository;

import com.publick.entity.Post;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.stream.Collectors;

@Repository
public class PostRepository extends InMemoryCrudRepository<Post, Long> {

    @Override
    protected Long getId(Post entity) {
        return entity.getId();
    }

    @Override
    protected void setId(Post entity, Long id) {
        entity.setId(id);
    }

    public List<Post> findByIssueId(Long issueId) {
        return storage.values().stream()
                .filter(post -> post.getIssueId().equals(issueId))
                .collect(Collectors.toList());
    }
}