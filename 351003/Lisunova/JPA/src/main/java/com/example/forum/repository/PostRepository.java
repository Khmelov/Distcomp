package com.example.forum.repository;

import com.example.forum.entity.Post;
import org.springframework.data.domain.Page;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.springframework.data.domain.Pageable;

import org.springframework.data.domain.Pageable;
@Repository
public interface PostRepository extends JpaRepository<Post, Long> {

    Page<Post> findByTopicId(Long topicId, Pageable pageable);
}
