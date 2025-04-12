package by.bsuir.distcomp.repository;

import by.bsuir.distcomp.entity.Reaction;
import by.bsuir.distcomp.entity.ReactionKey;
import org.springframework.data.cassandra.repository.CassandraRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ReactionRepository extends CassandraRepository<Reaction, ReactionKey> { }
