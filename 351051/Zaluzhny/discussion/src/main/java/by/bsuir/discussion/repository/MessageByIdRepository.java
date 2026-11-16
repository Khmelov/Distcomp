package by.bsuir.discussion.repository;

import by.bsuir.discussion.entity.MessageById;
import org.springframework.data.cassandra.repository.CassandraRepository;

public interface MessageByIdRepository extends CassandraRepository<MessageById, Long> {
}
