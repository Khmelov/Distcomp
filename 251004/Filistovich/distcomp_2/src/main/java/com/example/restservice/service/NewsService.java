package com.example.restservice.service;

import com.example.restservice.model.News;
import com.example.restservice.repository.NewsRepository;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class NewsService {
    private final NewsRepository repository;

    @Autowired
    public NewsService(NewsRepository repository) {
        this.repository = repository;
    }

    public List<News> findAll() {
        return repository.findAll();
    }

    public News findById(Long id) {
        return repository.getNewsById(id);
    }

    public News save(News news) {
        return repository.save(news);
    }

    @Transactional
    public void deleteById(Long id) {
        repository.deleteNewsById(id);
    }

    public News update(News news) {
        return repository.save(news);
    }

    /*public boolean existsByLogin(String login) {
        return repository.existsByLogin(login);
    }*/
}
