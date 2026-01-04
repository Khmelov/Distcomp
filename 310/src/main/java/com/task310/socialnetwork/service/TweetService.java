package com.task310.socialnetwork.service;

import com.task310.socialnetwork.dto.request.TweetRequestTo;
import com.task310.socialnetwork.dto.response.TweetResponseTo;
import java.util.List;

public interface TweetService {
    List<TweetResponseTo> getAll();
    TweetResponseTo getById(Long id);
    TweetResponseTo create(TweetRequestTo request);
    TweetResponseTo update(Long id, TweetRequestTo request);
    void delete(Long id);
    boolean existsById(Long id);
    List<TweetResponseTo> getByUserId(Long userId);
    List<TweetResponseTo> getByLabelId(Long labelId);
}