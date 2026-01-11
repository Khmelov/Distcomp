package com.distcomp.discussion.post.store;

import com.distcomp.discussion.post.dto.PostRequest;
import com.distcomp.discussion.post.dto.PostResponse;
import java.util.List;
import java.util.Optional;

public interface PostStore {
    PostResponse create(PostRequest request);

    List<PostResponse> listAll();

    Optional<PostResponse> getById(Long id);

    Optional<PostResponse> updateById(Long id, PostRequest request);

    boolean deleteById(Long id);

    List<PostResponse> listByArticle(String country, long articleId);
}
