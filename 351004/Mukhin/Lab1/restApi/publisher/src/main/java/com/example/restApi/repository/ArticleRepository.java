package com.example.restApi.repository;

import com.example.restApi.dto.response.ArticleResponseTo;
import com.example.restApi.model.Article;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.stream.Collectors;

@Repository
public interface ArticleRepository extends JpaRepository<Article, Long> {

    Page<Article> findByUser_Id(Long id, Pageable pageable);
    Page<Article> findByMarkers_Id(Long markerId, Pageable pageable);

}
