package com.example.demo.cassandra.service;

import com.example.demo.cassandra.model.AuthorCassandra;
import com.example.demo.cassandra.repository.AuthorCassandraRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.Optional;
import org.springframework.context.annotation.Profile;

@Service
@Profile("cassandra")
public class AuthorCassandraService {
    @Autowired
    private AuthorCassandraRepository repository;

    public AuthorCassandra save(AuthorCassandra author) {
        return repository.save(author);
    }

    public void deleteById(Long id) {
        repository.deleteById(id);
    }

    public Optional<AuthorCassandra> findById(Long id) {
        return repository.findById(id);
    }

    public List<AuthorCassandra> findAll() {
        return repository.findAll();
    }
}