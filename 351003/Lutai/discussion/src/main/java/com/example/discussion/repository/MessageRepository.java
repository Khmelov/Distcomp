package com.example.discussion.repository;

import com.example.discussion.model.Message;
import com.example.discussion.model.MessageKey;
import org.springframework.data.cassandra.repository.AllowFiltering;
import org.springframework.data.cassandra.repository.CassandraRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface MessageRepository extends CassandraRepository<Message, MessageKey> {

    @AllowFiltering
    List<Message> findAllByKeyArticleId(Long articleId);

    @AllowFiltering
    Optional<Message> findByKeyId(Long id);
}