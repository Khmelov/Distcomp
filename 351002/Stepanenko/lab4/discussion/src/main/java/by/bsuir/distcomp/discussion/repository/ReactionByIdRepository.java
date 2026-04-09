package by.bsuir.distcomp.discussion.repository;

import by.bsuir.distcomp.discussion.domain.ReactionById;
import org.springframework.data.cassandra.repository.CassandraRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ReactionByIdRepository extends CassandraRepository<ReactionById, Long> {
}