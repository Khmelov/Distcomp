package com.example.distcomp.service;

import com.example.distcomp.dto.IssueRequestTo;
import com.example.distcomp.dto.IssueResponseTo;
import com.example.distcomp.exception.ServiceException;
import com.example.distcomp.mapper.IssueMapper;
import com.example.distcomp.model.Issue;
import com.example.distcomp.model.Label;
import com.example.distcomp.repository.IssueRepository;
import com.example.distcomp.repository.LabelRepository;
import com.example.distcomp.repository.WriterRepository;
import com.example.distcomp.utils.ValidationUtils;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.sql.Timestamp;
import java.util.concurrent.TimeUnit;
import java.util.List;


@Service
@RequiredArgsConstructor
public class IssueService {
    private final IssueRepository issueRepository;
    private final WriterRepository writerRepository;
    private final LabelRepository labelRepository;
    private final IssueMapper issueMapper;
    ObjectMapper objectMapper = new ObjectMapper();
    private final RedisTemplate<String, Object> redisTemplate;

    private static final String CACHE_NAME = "issues";
    private static final long CACHE_TTL_MINUTES = 30;

    public IssueResponseTo createIssue(IssueRequestTo request) {
        validateIssueRequest(request);
        Issue entity = issueMapper.toEntity(request);
        for (Label leb : entity.getLabels())
        {
            labelRepository.save(leb);
        }
        entity.setWriter(writerRepository.getReferenceById(request.getWriterId()));
        entity.setCreated(new Timestamp(System.currentTimeMillis()));
        entity.setModified(new Timestamp(System.currentTimeMillis()));
        try{
            entity = issueRepository.save(entity);
            IssueResponseTo response = issueMapper.toResponse(entity);
            redisTemplate.delete("allIssues");
            redisTemplate.opsForValue().set(
                    "issue:" + entity.getId(),
                    response,
                    CACHE_TTL_MINUTES,
                    TimeUnit.MINUTES
            );
            return response;

        }
        catch (DataIntegrityViolationException e) {
            throw new ServiceException("Data integrity violation", 403);
        }
    }
    public List<IssueResponseTo> getAllIssues() {
        String key = "allIssues";
        Object cached = redisTemplate.opsForValue().get(key);

        if (cached != null) {
            return objectMapper.convertValue(cached, new TypeReference<List<IssueResponseTo>>() {});
        }

        List<IssueResponseTo> responseList = issueMapper.listToResponse(issueRepository.findAll());
        redisTemplate.opsForValue().set(key, responseList, CACHE_TTL_MINUTES, TimeUnit.MINUTES);
        return responseList;
    }
    public IssueResponseTo getIssueById(Long id) {
        String key = "issue:" + id;
        Object cached = redisTemplate.opsForValue().get(key);

        if (cached != null) {
            return objectMapper.convertValue(cached, IssueResponseTo.class);
        }
        Issue issue = issueRepository.findById(id)
                .orElseThrow(() -> new ServiceException("Issue not found with id: " + id, 404));
        IssueResponseTo response = issueMapper.toResponse(issue);
        redisTemplate.opsForValue().set(
                "issue:" + issue.getId(),
                response,
                CACHE_TTL_MINUTES,
                TimeUnit.MINUTES
        );
        return response;
    }

    public IssueResponseTo updateIssue(IssueRequestTo request) {
        validateIssueRequest(request);
        Issue entity = issueMapper.toEntity(request);
        for (Label leb : entity.getLabels())
        {
            labelRepository.save(leb);
        }
        Issue existingIssue = issueRepository.findById(request.getId())
                .orElseThrow(() -> new ServiceException("Issue not found with id: " + request.getId(), 404));
        entity.setWriter(writerRepository.getReferenceById(request.getWriterId()));
        entity.setCreated(existingIssue.getCreated());
        entity.setModified(new Timestamp(System.currentTimeMillis()));
        try{
            entity = issueRepository.save(entity);
            IssueResponseTo response = issueMapper.toResponse(entity);
            redisTemplate.delete("allIssues");
            redisTemplate.opsForValue().set(
                    "issue:" + entity.getId(),
                    response,
                    CACHE_TTL_MINUTES,
                    TimeUnit.MINUTES
            );
            return response;
        }
        catch (DataIntegrityViolationException e) {
            throw new ServiceException("Data integrity violation", 403);
        }
    }

    public void deleteIssue(Long id) {
        if (!issueRepository.existsById(id)) {
            throw new ServiceException("Issue not found with id: " + id, 404);
        }
        issueRepository.deleteById(id);
        redisTemplate.delete("issue:" + id);
        redisTemplate.delete("allIssues");
    }
    private void validateIssueRequest(IssueRequestTo request) {
        ValidationUtils.validateNotNull(request, "Issue");
        ValidationUtils.validateString(request.getTitle(), "Title", 2, 64);
        ValidationUtils.validateString(request.getContent(), "Content", 4, 2048);
        ValidationUtils.validateNotNegative(request.getWriterId(), "Writer ID");
    }
}
