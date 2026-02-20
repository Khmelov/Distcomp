package org.example.newsapi.repository.impl;

import org.example.newsapi.entity.Marker;
import org.example.newsapi.repository.InMemoryRepository;
import org.springframework.stereotype.Repository;

@Repository
public class MarkerRepository extends InMemoryRepository<Marker> {
}