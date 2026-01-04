package com.blog.repository;

import com.blog.model.Message;
import java.util.List;

public interface MessageRepository extends CrudRepository<Message, Long> {
    List<Message> findByTopicId(Long topicId);
}