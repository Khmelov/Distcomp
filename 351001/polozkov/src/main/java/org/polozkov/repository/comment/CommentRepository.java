package org.polozkov.repository.comment;

import org.polozkov.entity.comment.Comment;
import org.polozkov.exception.BadRequestException;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CommentRepository extends JpaRepository<Comment, Long> {

    default Comment byId(Long id) {
        return findById(id).orElseThrow(() -> new BadRequestException("Comment with id "  + id + " does not exists "));
    }

}