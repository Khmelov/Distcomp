package com.sergey.orsik.service;

import com.sergey.orsik.dto.request.TweetRequestTo;
import com.sergey.orsik.dto.response.TweetResponseTo;

import java.util.List;

public interface TweetService {

    List<TweetResponseTo> findAll(int page, int size, String sortBy, String sortDir, Long creatorId, String title);

    TweetResponseTo findById(Long id);

    TweetResponseTo create(TweetRequestTo request);

    TweetResponseTo update(Long id, TweetRequestTo request);

    void deleteById(Long id);
}
