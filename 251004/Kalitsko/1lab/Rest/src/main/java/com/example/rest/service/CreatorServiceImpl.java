package com.example.rest.service;

import com.example.rest.dto.CreatorRequestTo;
import com.example.rest.dto.CreatorResponseTo;
import com.example.rest.dto.CreatorUpdate;
import com.example.rest.entity.Creator;
import com.example.rest.mapper.CreatorMapper;
import com.example.rest.repository.CreatorRepository;
import lombok.AllArgsConstructor;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class CreatorServiceImpl implements CreatorService {

    private  CreatorRepository creatorRepository;
    private  CreatorMapper creatorMapper;

    @Autowired
    public CreatorServiceImpl(CreatorRepository creatorRepository, CreatorMapper creatorMapper) {
        this.creatorRepository = creatorRepository;
        this.creatorMapper = creatorMapper;
    }

    @Override
    public CreatorResponseTo create(CreatorRequestTo creatorRequestTo) {
        return creatorMapper.toCreatorResponseTo(creatorRepository.create(creatorMapper.toCreator(creatorRequestTo)));
    }

    @Override
    public CreatorResponseTo update(CreatorUpdate updatedCreator) {
        Creator creator = creatorRepository.findById(updatedCreator.getId())
                .orElseThrow(() -> new IllegalArgumentException("Creator not found"));

        if (updatedCreator.getId() != null) {
            creator.setId(updatedCreator.getId());
        }
        if (updatedCreator.getLogin() != null) {
            creator.setLogin(updatedCreator.getLogin());
        }
        if (updatedCreator.getPassword() != null) {
            creator.setPassword(updatedCreator.getPassword());
        }
        if (updatedCreator.getFirstname() != null) {
            creator.setFirstname(updatedCreator.getFirstname());
        }
        if (updatedCreator.getLastname() != null) {
            creator.setLastname(updatedCreator.getLastname());
        }

        return creatorMapper.toCreatorResponseTo(creatorRepository.update(creator));
    }

    @Override
    public void deleteById(Long id) {
        creatorRepository.deleteById(id);
    }

    @Override
    public List<CreatorResponseTo> findAll() {
        return creatorRepository.findAll()
                .stream()
                .map(creatorMapper::toCreatorResponseTo)
                .toList();
    }

    @Override
    public Optional<CreatorResponseTo> findById(Long id) {
        return creatorRepository.findById(id)
                .map(creatorMapper::toCreatorResponseTo);
    }
}
