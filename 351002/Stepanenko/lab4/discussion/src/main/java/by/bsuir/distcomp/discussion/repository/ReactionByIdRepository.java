package by.bsuir.distcomp.discussion.repository;

import by.bsuir.distcomp.discussion.domain.ReactionById;
import org.springframework.data.cassandra.repository.CassandraRepository;

public interface ReactionByIdRepository extends CassandraRepository<ReactionById, Long> {
}
