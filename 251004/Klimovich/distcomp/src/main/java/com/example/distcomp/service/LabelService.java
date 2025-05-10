package com.example.distcomp.service;

import com.example.distcomp.dto.LabelRequestTo;
import com.example.distcomp.dto.LabelResponseTo;
import com.example.distcomp.exception.ServiceException;
import com.example.distcomp.mapper.LabelMapper;
import com.example.distcomp.model.Label;
import com.example.distcomp.repository.LabelRepository;
import com.example.distcomp.utils.ValidationUtils;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.util.concurrent.TimeUnit;
import java.util.List;

@Service
@RequiredArgsConstructor
public class LabelService {
    private final LabelRepository labelRepository;
    private final LabelMapper labelMapper;
    private final RedisTemplate<String, Object> redisTemplate;
    private final ObjectMapper objectMapper = new ObjectMapper();

    private static final long CACHE_TTL_MINUTES = 30;

    public LabelResponseTo createLabel(LabelRequestTo request) {
        validateLabelRequest(request);
        Label entity = labelMapper.toEntity(request);
        LabelResponseTo response = labelMapper.toResponse(labelRepository.save(entity));

        redisTemplate.delete("allLabels");
        redisTemplate.opsForValue().set(
                "label:" + response.getId(),
                response,
                CACHE_TTL_MINUTES,
                TimeUnit.MINUTES
        );
        return response;
    }

    public List<LabelResponseTo> getAllLabels() {
        String key = "allLabels";
        Object cached = redisTemplate.opsForValue().get(key);

        if (cached != null) {
            return objectMapper.convertValue(cached, new TypeReference<List<LabelResponseTo>>() {});
        }

        List<LabelResponseTo> responseList = labelMapper.listToResponse(labelRepository.findAll());
        redisTemplate.opsForValue().set(key, responseList, CACHE_TTL_MINUTES, TimeUnit.MINUTES);
        return responseList;
    }

    public LabelResponseTo getLabelById(Long id) {
        String key = "label:" + id;
        Object cached = redisTemplate.opsForValue().get(key);

        if (cached != null) {
            return objectMapper.convertValue(cached, LabelResponseTo.class);
        }

        Label label = labelRepository.findById(id)
                .orElseThrow(() -> new ServiceException("Label not found with id: " + id, 404));

        LabelResponseTo response = labelMapper.toResponse(label);
        redisTemplate.opsForValue().set(key, response, CACHE_TTL_MINUTES, TimeUnit.MINUTES);
        return response;
    }

    public LabelResponseTo updateLabel(LabelRequestTo request) {
        validateLabelRequest(request);
        Label entity = labelMapper.toEntity(request);
        if (!labelRepository.existsById(entity.getId())) {
            throw new ServiceException("Label not found with id: " + entity.getId(), 404);
        }
        LabelResponseTo response = labelMapper.toResponse(labelRepository.save(entity));

        redisTemplate.delete("label:" + entity.getId());
        redisTemplate.delete("allLabels");
        redisTemplate.opsForValue().set(
                "label:" + entity.getId(),
                response,
                CACHE_TTL_MINUTES,
                TimeUnit.MINUTES
        );
        return response;
    }

    public void deleteLabel(Long id) {
        if (!labelRepository.existsById(id)) {
            throw new ServiceException("Label not found with id: " + id, 404);
        }
        labelRepository.deleteById(id);
        redisTemplate.delete("label:" + id);
        redisTemplate.delete("allLabels");
    }

    private void validateLabelRequest(LabelRequestTo request) {
        ValidationUtils.validateNotNull(request, "Label");
        ValidationUtils.validateString(request.getName(), "Name", 2, 32);
    }
}
