package by.bsuir.publisher.repositories;

import by.bsuir.publisher.domain.Comment;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CommentCacheRepository extends CrudRepository<Comment, Long> {
}
