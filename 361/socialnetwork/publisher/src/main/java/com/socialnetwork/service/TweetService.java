package com.socialnetwork.service;

import com.socialnetwork.dto.request.TweetRequestTo;
import com.socialnetwork.dto.response.TweetResponseTo;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import java.util.List;

public interface TweetService {
    List<TweetResponseTo> getAll();
    Page<TweetResponseTo> getAll(Pageable pageable);
    TweetResponseTo getById(Long id);
    TweetResponseTo create(TweetRequestTo request);
    TweetResponseTo update(Long id, TweetRequestTo request);
    void delete(Long id);
    boolean existsById(Long id);
    List<TweetResponseTo> getByUserId(Long userId);
    List<TweetResponseTo> getByLabelId(Long labelId);
    Page<TweetResponseTo> getByUserId(Long userId, Pageable pageable);
    Page<TweetResponseTo> getByLabelId(Long labelId, Pageable pageable);
}