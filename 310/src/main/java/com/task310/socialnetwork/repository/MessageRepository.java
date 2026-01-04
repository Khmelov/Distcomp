package com.task310.socialnetwork.repository;

import com.task310.socialnetwork.model.Message;
import java.util.List;

public interface MessageRepository extends CrudRepository<Message, Long> {
    List<Message> findByTweetId(Long tweetId);
}