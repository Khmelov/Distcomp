package com.adashkevich.nosql.lab.discussion.repository;

import com.adashkevich.nosql.lab.discussion.model.Message;
import com.adashkevich.nosql.lab.discussion.model.MessageKey;
import org.springframework.data.cassandra.repository.CassandraRepository;

import java.util.List;

public interface MessageRepository extends CassandraRepository<Message, MessageKey> {
    List<Message> findByKeyId(Long id);
    List<Message> findByKeyNewsId(Long newsId);
}
