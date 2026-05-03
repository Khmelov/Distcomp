package by.bsuir.task310.repository;

import by.bsuir.task310.model.Reaction;
import org.springframework.data.cassandra.repository.CassandraRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ReactionRepository extends CassandraRepository<Reaction, Long> {
}