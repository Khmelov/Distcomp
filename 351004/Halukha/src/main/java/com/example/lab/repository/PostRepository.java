package com.example.lab.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.lab.model.Post;

public interface PostRepository extends JpaRepository<Post, Long> {
}
