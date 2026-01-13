package com.distcomp.discussion.post.service;

import com.distcomp.discussion.post.dto.PostRequest;
import com.distcomp.discussion.post.dto.PostResponse;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.concurrent.atomic.AtomicLong;

@Service("postService")
public class MockPostService {

    private final Map<Long, PostResponse> posts = new HashMap<>();
    private final AtomicLong idGenerator = new AtomicLong(1);

    public PostResponse create(PostRequest request) {
        Long id = request.getId() != null ? request.getId() : idGenerator.getAndIncrement();
        idGenerator.accumulateAndGet(id + 1, Math::max);
        PostResponse response = new PostResponse();
        response.setId(id);
        response.setArticleId(request.getArticleId());
        response.setContent(request.getContent());
        response.setCountry(request.getCountry() != null ? request.getCountry() : "by");
        posts.put(id, response);
        return response;
    }

    public List<PostResponse> listAll() {
        return new ArrayList<>(posts.values());
    }

    public Optional<PostResponse> getById(Long id) {
        return Optional.ofNullable(posts.get(id));
    }

    public List<PostResponse> listByArticle(String country, long articleId) {
        return posts.values().stream()
                .filter(post -> post.getCountry().equals(country) && post.getArticleId() == articleId)
                .toList();
    }

    public Optional<PostResponse> updateById(Long id, PostRequest request) {
        PostResponse existing = posts.get(id);
        if (existing == null) {
            PostResponse created = new PostResponse();
            created.setId(id);
            created.setArticleId(request.getArticleId());
            created.setContent(request.getContent());
            created.setCountry(request.getCountry() != null ? request.getCountry() : "by");
            posts.put(id, created);
            return Optional.of(created);
        }

        existing.setContent(request.getContent());
        existing.setArticleId(request.getArticleId());
        if (request.getCountry() != null) {
            existing.setCountry(request.getCountry());
        }
        return Optional.of(existing);
    }

    public boolean deleteById(Long id) {
        return posts.remove(id) != null;
    }
}
