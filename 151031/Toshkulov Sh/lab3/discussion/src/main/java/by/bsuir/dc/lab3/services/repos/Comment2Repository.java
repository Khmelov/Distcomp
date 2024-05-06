package by.bsuir.dc.lab3.services.repos;

import by.bsuir.dc.lab3.entities.Comment;
import org.springframework.data.cassandra.repository.CassandraRepository;
import org.springframework.data.cassandra.repository.Query;

import java.util.Optional;

public interface Comment2Repository extends CassandraRepository<Comment, Long> {
    @Query("select * from comments where id = ?0 allow filtering;")
    Optional<Comment> findById(long id);
}
