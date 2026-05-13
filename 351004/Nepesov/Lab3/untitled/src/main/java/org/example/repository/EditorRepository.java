package org.example.repository;

import org.example.model.Editor;
import org.springframework.data.cassandra.repository.CassandraRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface EditorRepository extends CassandraRepository<Editor, Long> {
}