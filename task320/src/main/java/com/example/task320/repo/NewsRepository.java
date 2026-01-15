package com.example.task320.repo;

import com.example.task320.domain.NewsEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface NewsRepository extends JpaRepository<NewsEntity, Long> {
    boolean existsByTitle(String title);
}
