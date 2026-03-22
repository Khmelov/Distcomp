package com.example.demo.repository;

import com.example.demo.model.*;
import org.springframework.data.cassandra.repository.CassandraRepository;
import org.springframework.data.cassandra.repository.Query;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Optional;

@Repository
public interface MessageRepository extends CassandraRepository<Message, MessageKey> {
    List<Message> findByKeyIssueId(Long issueId);

    @Query("SELECT * FROM tbl_message WHERE id = ?0 ALLOW FILTERING")
    Optional<Message> findByIdOnly(Long id);
}
