package com.example.discussion.service;



import com.example.discussion.dto.CreatorRequestTo;
import com.example.discussion.dto.CreatorResponseTo;
import com.example.discussion.error.DuplicateLoginException;
import com.example.discussion.mapper.CreatorMapper;
import com.example.discussion.model.Creator;
import com.example.discussion.repository.CreatorRepository;
import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import org.mapstruct.factory.Mappers;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@Transactional
public class CreatorService {

    private final CreatorRepository creatorRepository;
    private final CreatorMapper creatorMapper = Mappers.getMapper(CreatorMapper.class);

    public CreatorService(CreatorRepository creatorRepository) {
        this.creatorRepository = creatorRepository;
    }

    public List<CreatorResponseTo> findAll() {
        return creatorRepository.findAll().stream()
                .map(creatorMapper::toDto)
                .collect(Collectors.toList());
    }

    @Transactional
    public CreatorResponseTo findById(Long id) {
        Optional<Creator> creator = creatorRepository.findById(id);
        return creator.map(creatorMapper::toDto).orElse(null);
    }

    @Transactional
    public CreatorResponseTo save(CreatorRequestTo creatorRequestTo) {
        if (creatorRepository.findByLogin(creatorRequestTo.getLogin()).isPresent()) {
            throw new DuplicateLoginException("Login already exists");
        }
        Creator creator = creatorMapper.toEntity(creatorRequestTo);
        Creator savedCreator = creatorRepository.save(creator);
        return creatorMapper.toDto(savedCreator);
    }

    @Transactional
    public CreatorResponseTo update(CreatorRequestTo creatorRequestTo) {
        Creator existingCreator = creatorRepository.findById(creatorRequestTo.getId()).orElseThrow(() -> new RuntimeException("Creator not found"));
        creatorMapper.updateEntityFromDto(creatorRequestTo, existingCreator);
        Creator updatedCreator = creatorRepository.save(existingCreator);
        return creatorMapper.toDto(updatedCreator);
    }

    public void deleteById(Long id) {
        if (!creatorRepository.existsById(id)) {
            throw new EntityNotFoundException("Creator not found with id " + id);
        }
        creatorRepository.deleteById(id);
    }
}