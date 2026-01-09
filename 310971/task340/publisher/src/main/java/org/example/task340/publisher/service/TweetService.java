package org.example.task340.publisher.service;

import java.util.List;
import org.example.task340.publisher.dto.TweetRequestTo;
import org.example.task340.publisher.dto.TweetResponseTo;
import org.example.task340.publisher.dto.LabelResponseTo;
import org.example.task340.publisher.dto.WriterResponseTo;

public interface TweetService {
    TweetResponseTo create(TweetRequestTo request);

    TweetResponseTo getById(Long id);

    List<TweetResponseTo> getAll();

    TweetResponseTo update(Long id, TweetRequestTo request);

    void delete(Long id);

    WriterResponseTo getWriterByTweetId(Long tweetId);

    List<LabelResponseTo> getLabelsByTweetId(Long tweetId);
}

