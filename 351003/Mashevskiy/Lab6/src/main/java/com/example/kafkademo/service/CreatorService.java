package com.example.kafkademo.service;

import com.example.kafkademo.entity.Creator;
import com.example.kafkademo.repository.CreatorRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.Optional;

@Service
public class CreatorService {

    @Autowired
    private CreatorRepository creatorRepository;

    public List<Creator> findAll() {
        return creatorRepository.findAll();
    }

    public Optional<Creator> findById(Long id) {
        return creatorRepository.findById(id);
    }

    public Optional<Creator> findByLogin(String login) {
        return creatorRepository.findByLogin(login);
    }

    public boolean existsByLogin(String login) {
        return creatorRepository.existsByLogin(login);
    }

    public Creator save(Creator creator) {
        return creatorRepository.save(creator);
    }

    public void deleteById(Long id) {
        if (creatorRepository.existsById(id)) {
            creatorRepository.deleteById(id);
        }
    }
}