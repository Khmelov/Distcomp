package org.example.repository;

import org.example.model.Editor;
import org.springframework.data.cassandra.repository.AllowFiltering;
import org.springframework.data.cassandra.repository.CassandraRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface EditorRepository extends CassandraRepository<Editor, Long> {

    @AllowFiltering
    List<Editor> findByLogin(String login); // Возвращаем список, это стабильнее
}