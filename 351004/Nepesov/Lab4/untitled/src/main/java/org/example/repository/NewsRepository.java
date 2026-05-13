package org.example.repository;

import org.example.model.News;
import org.springframework.data.cassandra.repository.CassandraRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface NewsRepository extends CassandraRepository<News, Long> { // Long вместо UUID
}