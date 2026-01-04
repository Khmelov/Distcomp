package com.task310.socialnetwork.service.impl;

import com.task310.socialnetwork.dto.request.LabelRequestTo;
import com.task310.socialnetwork.dto.response.LabelResponseTo;
import com.task310.socialnetwork.mapper.LabelMapper;
import com.task310.socialnetwork.model.Label;
import com.task310.socialnetwork.repository.LabelRepository;
import com.task310.socialnetwork.service.LabelService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.stream.Collectors;

@Service
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
    public LabelResponseTo getById(Long id) {
        Label label = labelRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Label not found with id: " + id));
        return labelMapper.toResponse(label);
    }

    @Override
    public LabelResponseTo create(LabelRequestTo request) {
        Label label = labelMapper.toEntity(request);
        Label saved = labelRepository.save(label);
        return labelMapper.toResponse(saved);
    }

    @Override
    public LabelResponseTo update(Long id, LabelRequestTo request) {
        if (!labelRepository.existsById(id)) {
            throw new RuntimeException("Label not found with id: " + id);
        }

        Label label = labelMapper.toEntity(request);
        label.setId(id);
        Label updated = labelRepository.update(label);
        return labelMapper.toResponse(updated);
    }

    @Override
    public void delete(Long id) {
        if (!labelRepository.deleteById(id)) {
            throw new RuntimeException("Label not found with id: " + id);
        }
    }

    @Override
    public boolean existsById(Long id) {
        return labelRepository.existsById(id);
    }
}