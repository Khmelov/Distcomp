package com.example.task361.service;

import com.example.task361.domain.dto.request.TweetRequestTo;
import com.example.task361.domain.dto.response.TweetResponseTo;
import java.util.List;

public interface TweetService {
    TweetResponseTo create(TweetRequestTo request);
    List<TweetResponseTo> findAll(int page, int size); 
    TweetResponseTo findById(Long id);
    TweetResponseTo update(TweetRequestTo request);
    void deleteById(Long id);
}