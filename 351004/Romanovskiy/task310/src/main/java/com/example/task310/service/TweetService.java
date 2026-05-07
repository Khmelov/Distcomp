package com.example.task310.service;

import com.example.task310.domain.dto.request.TweetRequestTo;
import com.example.task310.domain.dto.response.TweetResponseTo;
import java.util.List;

public interface TweetService {
    TweetResponseTo create(TweetRequestTo request);
    List<TweetResponseTo> findAll();
    TweetResponseTo findById(Long id);
    TweetResponseTo update(TweetRequestTo request);
    void deleteById(Long id);
}