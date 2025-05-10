package com.homel.user_stories.service;

import com.homel.user_stories.dto.LabelRequestTo;
import com.homel.user_stories.dto.LabelResponseTo;
import com.homel.user_stories.exception.EntityNotFoundException;
import com.homel.user_stories.mapper.LabelMapper;
import com.homel.user_stories.model.Label;
import com.homel.user_stories.repository.LabelRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class LabelService {
    private final LabelRepository labelRepository;
    private final RedisCacheService cacheService;

    private static final String LABEL_CACHE_PREFIX = "label::";

    @Autowired
    public LabelService(LabelRepository labelRepository, RedisCacheService cacheService) {
        this.labelRepository = labelRepository;
        this.cacheService = cacheService;
    }

    public LabelResponseTo createLabel(LabelRequestTo labelRequest) {
        Label label = LabelMapper.INSTANCE.toEntity(labelRequest);
        Label savedLabel = labelRepository.save(label);
        LabelResponseTo response = LabelMapper.INSTANCE.toResponse(savedLabel);
        cacheService.put(LABEL_CACHE_PREFIX + savedLabel.getId(), response);
        return response;
    }

    public LabelResponseTo getLabel(Long id) {
        String cacheKey = LABEL_CACHE_PREFIX + id;
        LabelResponseTo cached = cacheService.get(cacheKey, LabelResponseTo.class);
        if (cached != null) {
            return cached;
        }

        LabelResponseTo response = labelRepository.findById(id)
                .map(LabelMapper.INSTANCE::toResponse)
                .orElseThrow(() -> new EntityNotFoundException("Label not found"));

        cacheService.put(cacheKey, response);
        return response;
    }

    public List<LabelResponseTo> getAllLabels() {
        return labelRepository.findAll().stream()
                .map(LabelMapper.INSTANCE::toResponse)
                .toList();
    }

    public void deleteLabel(Long id) {
        labelRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Label with id " + id + " not found"));

        labelRepository.deleteById(id);
        cacheService.evict(LABEL_CACHE_PREFIX + id);
    }

    public LabelResponseTo updateLabel(LabelRequestTo labelRequest) {
        Label existingLabel = labelRepository.findById(labelRequest.getId())
                .orElseThrow(() -> new EntityNotFoundException("Label not found"));

        existingLabel.setName(labelRequest.getName());
        Label updatedLabel = labelRepository.save(existingLabel);

        LabelResponseTo response = LabelMapper.INSTANCE.toResponse(updatedLabel);
        cacheService.put(LABEL_CACHE_PREFIX + updatedLabel.getId(), response);
        return response;
    }
}
