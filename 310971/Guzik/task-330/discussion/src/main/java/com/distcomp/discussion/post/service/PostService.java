package com.distcomp.discussion.post.service;

import com.distcomp.discussion.post.dto.PostRequest;
import com.distcomp.discussion.post.dto.PostResponse;
import com.distcomp.discussion.post.store.PostStore;
import java.util.List;
import java.util.Optional;
import org.springframework.stereotype.Service;

@Service
public class PostService {

    private final PostStore store;

    public PostService(PostStore store) {
        this.store = store;
    }

    public PostResponse create(PostRequest request) {
        return store.create(request);
    }

    public List<PostResponse> listAll() {
        return store.listAll();
    }

    public Optional<PostResponse> getById(Long id) {
        return store.getById(id);
    }

    public List<PostResponse> listByArticle(String country, long articleId) {
        return store.listByArticle(country, articleId);
    }

    public Optional<PostResponse> updateById(Long id, PostRequest request) {
        return store.updateById(id, request);
    }

    public boolean deleteById(Long id) {
        return store.deleteById(id);
    }
}
