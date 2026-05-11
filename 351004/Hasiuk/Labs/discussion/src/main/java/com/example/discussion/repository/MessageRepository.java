package com.example.discussion.repository;

import com.example.discussion.entity.Message;
import com.example.discussion.entity.MessageKey;
import org.springframework.data.cassandra.repository.CassandraRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface MessageRepository extends CassandraRepository<Message, MessageKey> {
    List<Message> findByKeyStoryId(Long storyId);
}