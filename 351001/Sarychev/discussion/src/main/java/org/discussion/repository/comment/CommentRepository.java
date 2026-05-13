package org.discussion.repository.comment;

import org.discussion.entity.comment.Comment;
import org.discussion.entity.comment.CommentKey;
import org.springframework.data.cassandra.repository.AllowFiltering;
import org.springframework.data.cassandra.repository.CassandraRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface CommentRepository extends CassandraRepository<Comment, CommentKey> {
    List<Comment> findByKeyCountryAndKeyIssueId(String country, Long issueId);

    @AllowFiltering
    Optional<Comment> findByKeyId(Long id);
}