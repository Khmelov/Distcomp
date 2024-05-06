package by.bsuir.dc.lab4.services.repos;

import by.bsuir.dc.lab4.entities.Comment;
import org.springframework.data.cassandra.repository.CassandraRepository;
import org.springframework.data.cassandra.repository.Query;

import java.util.Optional;

public interface CommentDicsRepository extends CassandraRepository<Comment, Long> {
    @Query("select * from comments where id = ?0 allow filtering;")
    Optional<Comment> findById(long id);
}
