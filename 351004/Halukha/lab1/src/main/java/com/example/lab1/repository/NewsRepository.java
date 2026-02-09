package com.example.lab1.repository;

import java.util.List;

import com.example.lab1.model.News;

public interface NewsRepository extends CrudRepository<News> {
    @Override
    List<News> getAllEntities();

    
}
