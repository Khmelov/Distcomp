package org.example.newsapi.repository.impl;

import org.example.newsapi.entity.Comment;
import org.example.newsapi.repository.InMemoryRepository;
import org.springframework.stereotype.Repository;

@Repository
public class CommentRepository extends InMemoryRepository<Comment> {
}