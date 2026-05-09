package com.example.demo.service;

import com.example.demo.entity.Author;
import com.example.demo.repository.AuthorRepository;
import com.example.demo.sync.SyncService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.context.annotation.Profile;
import org.springframework.transaction.annotation.Transactional;
import com.example.demo.exception.NotFoundException;
@Profile("docker")
@Service
public class AuthorService extends AbstractGenericService<Author, Long> {
    private final AuthorRepository authorRepository;
    @Autowired
    private SyncService syncService;

    public AuthorService(AuthorRepository repository) {
        super(repository, repository);
        this.authorRepository = repository;
    }

    @Override
    public Author create(Author entity) {
        Author saved = super.create(entity);
        syncService.syncAuthor(saved, "create");
        return saved;
    }

    @Override
    public Author update(Author entity) {
        Author updated = super.update(entity);
        syncService.syncAuthor(updated, "update");
        return updated;
    }

    @Override
    @Transactional
    public void delete(Long id) {
        if (!authorRepository.existsById(id)) {
            throw new NotFoundException("Author not found with id: " + id);
        }
        Author existing = authorRepository.findById(id).get();
        authorRepository.deleteById(id);
        syncService.syncAuthor(existing, "delete");
    }
}