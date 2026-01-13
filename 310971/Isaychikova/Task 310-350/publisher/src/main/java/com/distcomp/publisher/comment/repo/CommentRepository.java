package com.distcomp.publisher.comment.repo;

import com.distcomp.publisher.comment.domain.Comment;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CommentRepository extends JpaRepository<Comment, Long> {

    List<Comment> findAllByStoryId(Long storyId);
}
