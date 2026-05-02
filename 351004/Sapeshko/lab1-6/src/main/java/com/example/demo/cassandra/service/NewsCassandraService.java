package com.example.demo.cassandra.service;

import com.example.demo.cassandra.model.NewsCassandra;
import com.example.demo.cassandra.repository.NewsCassandraRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.Optional;
import org.springframework.context.annotation.Profile;

@Service
@Profile("cassandra")
public class NewsCassandraService {
    @Autowired
    private NewsCassandraRepository repository;

    public NewsCassandra save(NewsCassandra news) {
        return repository.save(news);
    }

    public void deleteById(Long id) {
        repository.deleteById(id);
    }

    public Optional<NewsCassandra> findById(Long id) {
        return repository.findById(id);
    }

    public List<NewsCassandra> findAll() {
        return repository.findAll();
    }
}