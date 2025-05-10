package com.example.discussion.repository;

import com.example.discussion.model.Post;
import org.springframework.data.jpa.repository.JpaRepository;

//@Repository
public interface PostRepository extends JpaRepository<Post, Long> {
}