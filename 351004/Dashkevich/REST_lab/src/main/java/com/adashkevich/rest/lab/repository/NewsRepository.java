package com.adashkevich.rest.lab.repository;

import com.adashkevich.rest.lab.model.News;
import org.springframework.stereotype.Repository;

@Repository
public class NewsRepository extends InMemoryCrudRepository<News> {}
