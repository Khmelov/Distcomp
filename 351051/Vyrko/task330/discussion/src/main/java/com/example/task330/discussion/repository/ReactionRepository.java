
package com.example.task330.discussion.repository;

import com.example.task330.discussion.model.Reaction;
import org.springframework.data.cassandra.repository.CassandraRepository;

// Репозиторий обеспечивает доступ к реакциям в Cassandra.
public interface ReactionRepository extends CassandraRepository<Reaction, Long> {
}
