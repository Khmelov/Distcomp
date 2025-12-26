package com.restApp.repository;

import com.restApp.model.Comment;
import org.springframework.stereotype.Repository;

@Repository
public class CommentRepository extends AbstractInMemoryRepository<Comment> {
}
