package com.socialnetwork.service.impl;

import com.socialnetwork.dto.request.LabelRequestTo;
import com.socialnetwork.dto.response.LabelResponseTo;
import com.socialnetwork.exception.ResourceNotFoundException;
import com.socialnetwork.mapper.LabelMapper;
import com.socialnetwork.model.Label;
import com.socialnetwork.repository.LabelRepository;
import com.socialnetwork.service.LabelService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional
public class LabelServiceImpl implements LabelService {

    @Autowired
    private LabelRepository labelRepository;

    @Autowired
    private LabelMapper labelMapper;

    @Override
    public List<LabelResponseTo> getAll() {
        return labelRepository.findAll().stream()
                .map(labelMapper::toResponse)
                .collect(Collectors.toList());
    }

    @Override
    public Page<LabelResponseTo> getAll(Pageable pageable) {
        return labelRepository.findAll(pageable)
                .map(labelMapper::toResponse);
    }

    @Override
    public LabelResponseTo getById(Long id) {
        Label label = labelRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Label not found with id: " + id));
        return labelMapper.toResponse(label);
    }

    @Override
    public LabelResponseTo create(LabelRequestTo request) {
        if (labelRepository.existsByName(request.getName())) {
            throw new IllegalArgumentException("Label with name '" + request.getName() + "' already exists");
        }

        Label label = labelMapper.toEntity(request);
        Label savedLabel = labelRepository.save(label);
        return labelMapper.toResponse(savedLabel);
    }

    @Override
    public LabelResponseTo update(Long id, LabelRequestTo request) {
        Label label = labelRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Label not found with id: " + id));

        if (!label.getName().equals(request.getName()) &&
                labelRepository.existsByName(request.getName())) {
            throw new IllegalArgumentException("Label with name '" + request.getName() + "' already exists");
        }

        label.setName(request.getName());
        Label updatedLabel = labelRepository.save(label);
        return labelMapper.toResponse(updatedLabel);
    }

    @Override
    public void delete(Long id) {
        Label label = labelRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Label not found with id: " + id));

        if (!label.getTweets().isEmpty()) {
            throw new IllegalStateException("Cannot delete label with id " + id + " because it is used in tweets");
        }

        labelRepository.delete(label);
    }

    @Override
    public boolean existsById(Long id) {
        return labelRepository.existsById(id);
    }

    @Override
    public LabelResponseTo findByName(String name) {
        Label label = labelRepository.findByName(name)
                .orElseThrow(() -> new ResourceNotFoundException("Label not found with name: " + name));
        return labelMapper.toResponse(label);
    }

    @Override
    public boolean existsByName(String name) {
        return labelRepository.existsByName(name);
    }
}