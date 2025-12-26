package com.restApp.service;

import com.restApp.dto.NewsRequestTo;
import com.restApp.dto.NewsResponseTo;

import java.util.List;

public interface NewsService {
    NewsResponseTo create(NewsRequestTo request);

    NewsResponseTo update(Long id, NewsRequestTo request);

    void delete(Long id);

    NewsResponseTo findById(Long id);

    List<NewsResponseTo> findAll();
}
