package by.bsuir.distcomp.discussion.cassandra;

import org.springframework.data.cassandra.repository.CassandraRepository;

public interface ReactionCassandraRepository extends CassandraRepository<ReactionRow, Long> {
}
