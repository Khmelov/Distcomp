package com.example.distcomp_1.service;

import com.example.distcomp_1.mdoel.Author;
import com.example.distcomp_1.mdoel.News;
import com.example.distcomp_1.repository.AuthorRepository;
import com.example.distcomp_1.repository.NewsRepository;
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
        return repository.getNews();
    }

    public News findById(Long id) {
        return repository.get(id);
    }

    public News save(News news) {
        return repository.add(news);
    }

    public void deleteById(Long id) {
        repository.delete(id);
    }

    public News update(News news) {
        return repository.update(news);
    }

    /*public boolean existsByLogin(String login) {
        return repository.existsByLogin(login);
    }*/
}
