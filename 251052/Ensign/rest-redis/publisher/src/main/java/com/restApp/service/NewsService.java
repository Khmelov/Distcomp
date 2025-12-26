package com.restApp.service;

import com.restApp.dto.NewsRequestTo;
import com.restApp.dto.NewsResponseTo;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface NewsService {
    NewsResponseTo create(NewsRequestTo request);

    NewsResponseTo update(Long id, NewsRequestTo request);

    void delete(Long id);

    NewsResponseTo findById(Long id);

    Page<NewsResponseTo> findAll(Pageable pageable);
}
