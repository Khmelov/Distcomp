package org.example.newsapi.repository.impl;

import org.example.newsapi.entity.User;
import org.example.newsapi.repository.InMemoryRepository;
import org.springframework.stereotype.Repository;

@Repository
public class UserRepository extends InMemoryRepository<User> {
}