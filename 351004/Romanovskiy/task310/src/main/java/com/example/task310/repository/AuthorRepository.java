package com.example.task310.repository;

import com.example.task310.domain.entity.Author;
import org.springframework.stereotype.Repository;

@Repository
public class AuthorRepository extends InMemoryRepository<Author> {
}