package com.example.distcomp.service;
import com.example.distcomp.dto.WriterRequestTo;
import com.example.distcomp.dto.WriterResponseTo;
import com.example.distcomp.exception.ServiceException;
import com.example.distcomp.mapper.WriterMapper;
import com.example.distcomp.model.Writer;
import com.example.distcomp.repository.WriterRepository;
import com.example.distcomp.utils.ValidationUtils;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.util.concurrent.TimeUnit;
import java.util.List;

@Service
@RequiredArgsConstructor
public class WriterService {
    private final WriterRepository writerRepository;
    private final WriterMapper writerMapper;
    private final RedisTemplate<String, Object> redisTemplate;
    private final ObjectMapper objectMapper = new ObjectMapper();

    private static final long CACHE_TTL_MINUTES = 30;

    public WriterResponseTo createWriter(WriterRequestTo request) {
        validateWriterRequest(request);
        try {
            Writer writer = writerRepository.save(writerMapper.toEntity(request));
            WriterResponseTo response = writerMapper.toResponse(writer);
            redisTemplate.delete("allWriters");
            redisTemplate.opsForValue().set(
                    "writer:" + writer.getId(),
                    response,
                    CACHE_TTL_MINUTES,
                    TimeUnit.MINUTES
            );
            return response;
        } catch (DataIntegrityViolationException e) {
            throw new ServiceException("Data integrity violation", 403);
        }
    }

    public List<WriterResponseTo> getAllWriters() {
        String key = "allWriters";
        Object cached = redisTemplate.opsForValue().get(key);

        if (cached != null) {
            return objectMapper.convertValue(cached, new TypeReference<List<WriterResponseTo>>() {});
        }

        List<WriterResponseTo> responseList = writerMapper.listToResponse(writerRepository.findAll());
        redisTemplate.opsForValue().set(key, responseList, CACHE_TTL_MINUTES, TimeUnit.MINUTES);
        return responseList;
    }

    public WriterResponseTo getWriterById(Long id) {
        String key = "writer:" + id;
        Object cached = redisTemplate.opsForValue().get(key);

        if (cached != null) {
            return objectMapper.convertValue(cached, WriterResponseTo.class);
        }

        Writer writer = writerRepository.findById(id)
                .orElseThrow(() -> new ServiceException("Writer not found with id: " + id, 404));
        WriterResponseTo response = writerMapper.toResponse(writer);
        redisTemplate.opsForValue().set(key, response, CACHE_TTL_MINUTES, TimeUnit.MINUTES);
        return response;
    }

    public WriterResponseTo updateWriter(WriterRequestTo request) {
        validateWriterRequest(request);
        Writer entity = writerMapper.toEntity(request);
        if (!writerRepository.existsById(entity.getId())) {
            throw new ServiceException("Writer not found with id: " + entity.getId(), 404);
        }
        try {
            Writer updated = writerRepository.save(entity);
            WriterResponseTo response = writerMapper.toResponse(updated);

            redisTemplate.delete("writer:" + entity.getId());
            redisTemplate.delete("allWriters");
            redisTemplate.opsForValue().set(
                    "writer:" + entity.getId(),
                    response,
                    CACHE_TTL_MINUTES,
                    TimeUnit.MINUTES
            );
            return response;
        } catch (DataIntegrityViolationException e) {
            throw new ServiceException("Data integrity violation", 403);
        }
    }

    public void deleteWriter(Long id) {
        if (!writerRepository.existsById(id)) {
            throw new ServiceException("Writer not found with id: " + id, 404);
        }
        writerRepository.deleteById(id);
        redisTemplate.delete("writer:" + id);
        redisTemplate.delete("allWriters");
    }

    private void validateWriterRequest(WriterRequestTo request) {
        ValidationUtils.validateNotNull(request, "Writer");
        ValidationUtils.validateString(request.getLogin(), "Login", 2, 64);
        ValidationUtils.validateString(request.getPassword(), "Password", 8, 128);
        ValidationUtils.validateString(request.getFirstname(), "First name", 2, 64);
        ValidationUtils.validateString(request.getLastname(), "Last name", 2, 64);
    }
}