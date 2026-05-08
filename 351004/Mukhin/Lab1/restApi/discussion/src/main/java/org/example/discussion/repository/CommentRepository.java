package org.example.discussion.repository;

import com.datastax.oss.driver.api.core.uuid.Uuids;
import org.example.discussion.model.Comment;
import org.springframework.data.cassandra.repository.CassandraRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CommentRepository extends CassandraRepository<Comment, Long> {
    List<Comment> findByArticleId(Long articleId);
}