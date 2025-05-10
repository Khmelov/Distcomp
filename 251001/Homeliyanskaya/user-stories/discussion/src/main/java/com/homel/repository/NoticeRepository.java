package com.homel.repository;

import com.homel.model.Notice;
import org.springframework.data.cassandra.repository.CassandraRepository;

import java.util.Optional;
import java.util.UUID;

public interface NoticeRepository extends CassandraRepository<Notice, UUID> {
    // Ты можешь добавлять свои запросы, например:
    // List<Notice> findByTitle(String title);
}
