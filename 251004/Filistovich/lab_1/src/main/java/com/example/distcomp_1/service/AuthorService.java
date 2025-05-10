package com.example.distcomp_1.service;

import com.example.distcomp_1.mdoel.Author;
import com.example.distcomp_1.repository.AuthorRepository;
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
        return repository.getAuthors();
    }

    public Author findById(Long id) {
        return repository.get(id);
    }

    public Author save(Author author) {
        return repository.add(author);
    }

    public void deleteById(Long id) {
        repository.delete(id);
    }

    public Author update(Author author) {
        return repository.update(author);
    }

    /*public boolean existsByLogin(String login) {
        return repository.existsByLogin(login);
    }*/
}
