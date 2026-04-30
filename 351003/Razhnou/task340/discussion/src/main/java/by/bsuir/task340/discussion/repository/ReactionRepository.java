package by.bsuir.task340.discussion.repository;

import by.bsuir.task340.discussion.entity.Reaction;
import org.springframework.data.cassandra.repository.CassandraRepository;

public interface ReactionRepository extends CassandraRepository<Reaction, Long> {
}
