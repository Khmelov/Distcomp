package com.example.lab.repository;

import java.util.List;

import com.example.lab.model.News;

public interface NewsRepository extends CrudRepository<News> {
    @Override
    List<News> getAllEntities();

    
}
