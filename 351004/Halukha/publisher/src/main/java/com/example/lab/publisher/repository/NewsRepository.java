package com.example.lab.publisher.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.lab.publisher.model.News;

public interface NewsRepository extends JpaRepository<News, Long> {
}
