package com.example.lab.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.lab.model.News;

public interface NewsRepository extends JpaRepository<News, Long> {
}
