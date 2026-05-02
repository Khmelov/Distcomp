package com.example.demo.cassandra.service;

import com.example.demo.cassandra.model.TagCassandra;
import com.example.demo.cassandra.repository.TagCassandraRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.Optional;
import org.springframework.context.annotation.Profile;

@Service
@Profile("cassandra")
public class TagCassandraService {
    @Autowired
    private TagCassandraRepository repository;

    public TagCassandra save(TagCassandra tag) {
        return repository.save(tag);
    }

    public void deleteById(Long id) {
        repository.deleteById(id);
    }

    public Optional<TagCassandra> findById(Long id) {
        return repository.findById(id);
    }

    public List<TagCassandra> findAll() {
        return repository.findAll();
    }
}