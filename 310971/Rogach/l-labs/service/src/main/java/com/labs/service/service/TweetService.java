package com.labs.service.service;

import com.labs.domain.entity.Label;
import com.labs.domain.entity.Tweet;
import com.labs.domain.entity.Writer;
import com.labs.domain.repository.LabelRepository;
import com.labs.domain.repository.TweetRepository;
import com.labs.domain.repository.WriterRepository;
import com.labs.service.dto.LabelDto;
import com.labs.service.dto.MessageDto;
import com.labs.service.dto.TweetDto;
import com.labs.service.dto.WriterDto;
import com.labs.service.exception.ResourceNotFoundException;
import com.labs.service.exception.ValidationException;
import com.labs.service.mapper.LabelMapper;
import com.labs.service.mapper.MessageMapper;
import com.labs.service.mapper.TweetMapper;
import com.labs.service.mapper.WriterMapper;
import jakarta.persistence.criteria.Predicate;
import lombok.RequiredArgsConstructor;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
public class TweetService {
    private final TweetRepository tweetRepository;
    private final WriterRepository writerRepository;
    private final LabelRepository labelRepository;
    private final TweetMapper tweetMapper;
    private final WriterMapper writerMapper;
    private final LabelMapper labelMapper;
    private final MessageMapper messageMapper;

    public TweetDto create(TweetDto tweetDto) {
        validateTweetDto(tweetDto);
        Writer writer = writerRepository.findById(tweetDto.getWriterId())
                .orElseThrow(() -> new ResourceNotFoundException("Writer with id " + tweetDto.getWriterId() + " not found"));
        
        Tweet tweet = tweetMapper.toEntity(tweetDto);
        tweet.setWriter(writer);
        LocalDateTime now = LocalDateTime.now();
        tweet.setCreated(now);
        tweet.setModified(now);
        
        Tweet saved = tweetRepository.save(tweet);
        return tweetMapper.toDto(saved);
    }

    @Transactional(readOnly = true)
    public List<TweetDto> findAll() {
        return tweetRepository.findAll().stream()
                .map(tweetMapper::toDto)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public TweetDto findById(Long id) {
        Tweet tweet = tweetRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Tweet with id " + id + " not found"));
        return tweetMapper.toDto(tweet);
    }

    public TweetDto update(Long id, TweetDto tweetDto) {
        validateTweetDto(tweetDto);
        Tweet tweet = tweetRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Tweet with id " + id + " not found"));
        
        Writer writer = writerRepository.findById(tweetDto.getWriterId())
                .orElseThrow(() -> new ResourceNotFoundException("Writer with id " + tweetDto.getWriterId() + " not found"));
        
        tweet.setWriter(writer);
        tweet.setTitle(tweetDto.getTitle());
        tweet.setContent(tweetDto.getContent());
        tweet.setModified(LocalDateTime.now());
        
        Tweet updated = tweetRepository.save(tweet);
        return tweetMapper.toDto(updated);
    }

    public void delete(Long id) {
        if (!tweetRepository.existsById(id)) {
            throw new ResourceNotFoundException("Tweet with id " + id + " not found");
        }
        tweetRepository.deleteById(id);
    }

    @Transactional(readOnly = true)
    public WriterDto findWriterByTweetId(Long tweetId) {
        Writer writer = tweetRepository.findWriterByTweetId(tweetId);
        if (writer == null) {
            throw new ResourceNotFoundException("Tweet with id " + tweetId + " not found");
        }
        return writerMapper.toDto(writer);
    }

    @Transactional(readOnly = true)
    public List<LabelDto> findLabelsByTweetId(Long tweetId) {
        if (!tweetRepository.existsById(tweetId)) {
            throw new ResourceNotFoundException("Tweet with id " + tweetId + " not found");
        }
        List<Label> labels = tweetRepository.findLabelsByTweetId(tweetId);
        return labels.stream()
                .map(labelMapper::toDto)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<MessageDto> findMessagesByTweetId(Long tweetId) {
        if (!tweetRepository.existsById(tweetId)) {
            throw new ResourceNotFoundException("Tweet with id " + tweetId + " not found");
        }
        return tweetRepository.findMessagesByTweetId(tweetId).stream()
                .map(messageMapper::toDto)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<TweetDto> findByFilters(List<String> labelNames, List<Long> labelIds, String writerLogin, 
                                        String title, String content) {
        Specification<Tweet> spec = (root, query, cb) -> {
            List<Predicate> predicates = new ArrayList<>();

            if (labelNames != null && !labelNames.isEmpty()) {
                List<Label> labels = labelRepository.findByNameIn(labelNames);
                if (!labels.isEmpty()) {
                    predicates.add(root.join("labels").in(labels));
                } else {
                    return cb.disjunction();
                }
            }

            if (labelIds != null && !labelIds.isEmpty()) {
                List<Label> labels = labelRepository.findAllById(labelIds);
                if (!labels.isEmpty()) {
                    predicates.add(root.join("labels").in(labels));
                } else {
                    return cb.disjunction();
                }
            }

            if (writerLogin != null && !writerLogin.trim().isEmpty()) {
                predicates.add(cb.equal(root.get("writer").get("login"), writerLogin));
            }

            if (title != null && !title.trim().isEmpty()) {
                predicates.add(cb.like(cb.lower(root.get("title")), "%" + title.toLowerCase() + "%"));
            }

            if (content != null && !content.trim().isEmpty()) {
                predicates.add(cb.like(cb.lower(root.get("content")), "%" + content.toLowerCase() + "%"));
            }

            if (predicates.isEmpty()) {
                return cb.conjunction();
            }
            return cb.and(predicates.toArray(new Predicate[0]));
        };

        return tweetRepository.findAll(spec).stream()
                .map(tweetMapper::toDto)
                .collect(Collectors.toList());
    }

    private void validateTweetDto(TweetDto tweetDto) {
        if (tweetDto == null) {
            throw new ValidationException("Tweet data cannot be null");
        }
        if (tweetDto.getWriterId() == null) {
            throw new ValidationException("Writer ID cannot be null");
        }
        if (tweetDto.getTitle() == null || tweetDto.getTitle().trim().isEmpty()) {
            throw new ValidationException("Title cannot be null or empty");
        }
        if (tweetDto.getTitle().length() < 2 || tweetDto.getTitle().length() > 64) {
            throw new ValidationException("Title must be between 2 and 64 characters");
        }
        if (tweetDto.getContent() == null || tweetDto.getContent().trim().isEmpty()) {
            throw new ValidationException("Content cannot be null or empty");
        }
        if (tweetDto.getContent().length() < 4 || tweetDto.getContent().length() > 2048) {
            throw new ValidationException("Content must be between 4 and 2048 characters");
        }
    }
}

