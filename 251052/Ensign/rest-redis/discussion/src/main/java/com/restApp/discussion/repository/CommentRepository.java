package com.restApp.discussion.repository;

import com.restApp.discussion.model.Comment;
import org.springframework.data.cassandra.repository.AllowFiltering;
import org.springframework.data.cassandra.repository.CassandraRepository;
import org.springframework.stereotype.Repository;
import java.util.Optional;
import java.util.List;

@Repository
public interface CommentRepository extends CassandraRepository<Comment, Long> {

    @AllowFiltering
    Optional<Comment> findOneById(Long id);

    Optional<Comment> findByCountryAndId(String country, Long id);

    List<Comment> findByNewsId(Long newsId);
}
