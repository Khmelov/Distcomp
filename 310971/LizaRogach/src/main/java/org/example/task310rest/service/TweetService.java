package org.example.task310rest.service;

import java.util.List;
import org.example.task310rest.dto.TweetRequestTo;
import org.example.task310rest.dto.TweetResponseTo;
import org.example.task310rest.dto.LabelResponseTo;
import org.example.task310rest.dto.MessageResponseTo;
import org.example.task310rest.dto.WriterResponseTo;

public interface TweetService {
    TweetResponseTo create(TweetRequestTo request);

    TweetResponseTo getById(Long id);

    List<TweetResponseTo> getAll();

    TweetResponseTo update(Long id, TweetRequestTo request);

    void delete(Long id);

    WriterResponseTo getWriterByTweetId(Long tweetId);

    List<LabelResponseTo> getLabelsByTweetId(Long tweetId);

    List<MessageResponseTo> getMessagesByTweetId(Long tweetId);
}


