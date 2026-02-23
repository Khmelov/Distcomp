package com.example.restapp.repository;

import com.example.restapp.model.Author;
import org.springframework.stereotype.Component;

@Component
public class AuthorRepository extends InMemoryRepository<Author> {
}