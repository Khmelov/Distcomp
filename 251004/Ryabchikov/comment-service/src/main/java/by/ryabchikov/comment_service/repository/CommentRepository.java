package by.ryabchikov.comment_service.repository;

import by.ryabchikov.comment_service.entity.Comment;
import org.springframework.data.cassandra.repository.AllowFiltering;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface CommentRepository extends CrudRepository<Comment, String> {

    @AllowFiltering
    Optional<Comment> findById(Long id);
}