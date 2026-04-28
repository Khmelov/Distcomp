package com.example.task340.repository;

import com.example.task340.domain.entity.Reaction;
import org.springframework.data.cassandra.repository.CassandraRepository; // ИМПОРТ ДЛЯ КАССАНДРЫ
import org.springframework.stereotype.Repository;

@Repository
public interface ReactionRepository extends CassandraRepository<Reaction, Long> {
    // В Cassandra мы используем CassandraRepository вместо JpaRepository
}