package com.example.demo.cassandra.service;

import com.example.demo.cassandra.model.CommentCassandra;
import com.example.demo.cassandra.repository.CommentCassandraRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.Optional;
import org.springframework.context.annotation.Profile;

@Service
@Profile("cassandra")
public class CommentCassandraService {
    @Autowired
    private CommentCassandraRepository repository;

    public CommentCassandra save(CommentCassandra comment) {
        return repository.save(comment);
    }

    public void deleteById(Long id) {
        repository.deleteById(id);
    }

    public Optional<CommentCassandra> findById(Long id) {
        return repository.findById(id);
    }

    public List<CommentCassandra> findAll() {
        return repository.findAll();
    }
}