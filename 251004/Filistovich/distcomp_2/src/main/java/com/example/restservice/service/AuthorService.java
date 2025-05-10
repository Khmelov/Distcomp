package com.example.restservice.service;

import com.example.restservice.model.Author;
import com.example.restservice.repository.AuthorRepository;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class AuthorService {
    private final AuthorRepository repository;

    @Autowired
    public AuthorService(AuthorRepository repository) {
        this.repository = repository;
    }

    public List<Author> findAll() {
        return repository.findAll();
    }

    public Author findById(Long id) {
        return repository.getAuthorById(id);
    }

    public Author save(Author author) {
        return repository.save(author);
    }

    @Transactional
    public void deleteById(Long id) {

        repository.deleteById(id);
    }

    public Author update(Author author) {
        return repository.save(author);
    }

}
