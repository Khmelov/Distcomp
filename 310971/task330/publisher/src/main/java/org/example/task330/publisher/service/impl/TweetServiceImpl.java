package org.example.task330.publisher.service.impl;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.stream.Collectors;
import org.example.task330.publisher.dto.LabelResponseTo;
import org.example.task330.publisher.dto.TweetRequestTo;
import org.example.task330.publisher.dto.TweetResponseTo;
import org.example.task330.publisher.dto.WriterResponseTo;
import org.example.task330.publisher.exception.ConflictException;
import org.example.task330.publisher.exception.NotFoundException;
import org.example.task330.publisher.exception.ValidationException;
import org.example.task330.publisher.mapper.LabelMapper;
import org.example.task330.publisher.mapper.TweetMapper;
import org.example.task330.publisher.mapper.WriterMapper;
import org.example.task330.publisher.model.Label;
import org.example.task330.publisher.model.Tweet;
import org.example.task330.publisher.model.TweetLabel;
import org.example.task330.publisher.model.Writer;
import org.example.task330.publisher.repository.LabelRepository;
import org.example.task330.publisher.repository.TweetLabelRepository;
import org.example.task330.publisher.repository.TweetRepository;
import org.example.task330.publisher.repository.WriterRepository;
import org.example.task330.publisher.service.TweetService;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

@Service
public class TweetServiceImpl implements TweetService {

    private final TweetRepository tweetRepository;
    private final WriterRepository writerRepository;
    private final LabelRepository labelRepository;
    private final TweetLabelRepository tweetLabelRepository;
    private final TweetMapper tweetMapper;
    private final WriterMapper writerMapper;
    private final LabelMapper labelMapper;

    public TweetServiceImpl(TweetRepository tweetRepository,
                            WriterRepository writerRepository,
                            LabelRepository labelRepository,
                            TweetLabelRepository tweetLabelRepository,
                            TweetMapper tweetMapper,
                            WriterMapper writerMapper,
                            LabelMapper labelMapper) {
        this.tweetRepository = tweetRepository;
        this.writerRepository = writerRepository;
        this.labelRepository = labelRepository;
        this.tweetLabelRepository = tweetLabelRepository;
        this.tweetMapper = tweetMapper;
        this.writerMapper = writerMapper;
        this.labelMapper = labelMapper;
    }

    @Override
    @Transactional
    public TweetResponseTo create(TweetRequestTo request) {
        validate(request);
        
        // Check writer exists
        Writer writer = writerRepository.findById(request.getWriterId())
                .orElseThrow(() -> new NotFoundException("Writer not found: " + request.getWriterId()));
        
        // Check for duplicate title
        if (tweetRepository.findByTitle(request.getTitle()).isPresent()) {
            throw new ConflictException("Tweet with title already exists: " + request.getTitle());
        }
        
        Tweet entity = tweetMapper.toEntity(request);
        entity.setWriter(writer);
        entity.setCreated(OffsetDateTime.now());
        entity.setModified(entity.getCreated());
        
        // Handle labels
        if (request.getLabelIds() != null && !request.getLabelIds().isEmpty()) {
            for (Long labelId : request.getLabelIds()) {
                Label label = labelRepository.findById(labelId)
                        .orElseThrow(() -> new NotFoundException("Label not found: " + labelId));
                TweetLabel tweetLabel = new TweetLabel();
                tweetLabel.setTweet(entity);
                tweetLabel.setLabel(label);
                entity.getTweetLabels().add(tweetLabel);
            }
        }
        
        try {
            tweetRepository.save(entity);
        } catch (DataIntegrityViolationException e) {
            throw new ConflictException("Tweet with title already exists: " + request.getTitle());
        }
        return tweetMapper.toDto(entity);
    }

    @Override
    public TweetResponseTo getById(Long id) {
        Tweet entity = tweetRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Tweet not found: " + id));
        return tweetMapper.toDto(entity);
    }

    @Override
    public List<TweetResponseTo> getAll() {
        return tweetRepository.findAll().stream().map(tweetMapper::toDto).collect(Collectors.toList());
    }

    @Override
    @Transactional
    public TweetResponseTo update(Long id, TweetRequestTo request) {
        validate(request);
        Tweet entity = tweetRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Tweet not found: " + id));
        
        Writer writer = writerRepository.findById(request.getWriterId())
                .orElseThrow(() -> new NotFoundException("Writer not found: " + request.getWriterId()));
        
        // Check for duplicate title if title changed
        if (!entity.getTitle().equals(request.getTitle())) {
            if (tweetRepository.findByTitle(request.getTitle()).isPresent()) {
                throw new ConflictException("Tweet with title already exists: " + request.getTitle());
            }
        }
        
        tweetMapper.updateEntityFromDto(request, entity);
        entity.setWriter(writer);
        entity.setModified(OffsetDateTime.now());
        
        // Update labels
        entity.getTweetLabels().clear();
        if (request.getLabelIds() != null && !request.getLabelIds().isEmpty()) {
            for (Long labelId : request.getLabelIds()) {
                Label label = labelRepository.findById(labelId)
                        .orElseThrow(() -> new NotFoundException("Label not found: " + labelId));
                TweetLabel tweetLabel = new TweetLabel();
                tweetLabel.setTweet(entity);
                tweetLabel.setLabel(label);
                entity.getTweetLabels().add(tweetLabel);
            }
        }
        
        try {
            tweetRepository.save(entity);
        } catch (DataIntegrityViolationException e) {
            throw new ConflictException("Tweet with title already exists: " + request.getTitle());
        }
        return tweetMapper.toDto(entity);
    }

    @Override
    public void delete(Long id) {
        Tweet entity = tweetRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Tweet not found: " + id));
        tweetRepository.deleteById(entity.getId());
    }

    @Override
    public WriterResponseTo getWriterByTweetId(Long tweetId) {
        Tweet tweet = tweetRepository.findById(tweetId)
                .orElseThrow(() -> new NotFoundException("Tweet not found: " + tweetId));
        Writer writer = tweet.getWriter();
        if (writer == null) {
            throw new NotFoundException("Writer not found for tweet: " + tweetId);
        }
        return writerMapper.toDto(writer);
    }

    @Override
    public List<LabelResponseTo> getLabelsByTweetId(Long tweetId) {
        Tweet tweet = tweetRepository.findById(tweetId)
                .orElseThrow(() -> new NotFoundException("Tweet not found: " + tweetId));
        return tweet.getTweetLabels().stream()
                .map(tl -> labelMapper.toDto(tl.getLabel()))
                .collect(Collectors.toList());
    }

    private void validate(TweetRequestTo request) {
        if (request == null) {
            throw new ValidationException("Tweet request cannot be null");
        }
        if (!StringUtils.hasText(request.getTitle()) || request.getTitle().length() < 2 || request.getTitle().length() > 64) {
            throw new ValidationException("Tweet title must be between 2 and 64 characters");
        }
        if (!StringUtils.hasText(request.getContent()) || request.getContent().length() < 4 || request.getContent().length() > 2048) {
            throw new ValidationException("Tweet content must be between 4 and 2048 characters");
        }
        if (request.getWriterId() == null) {
            throw new ValidationException("Tweet writerId cannot be null");
        }
        if (request.getLabelIds() != null && !request.getLabelIds().isEmpty()) {
            for (Long id : request.getLabelIds()) {
                if (id == null || !labelRepository.existsById(id)) {
                    throw new NotFoundException("Label not found: " + id);
                }
            }
        }
    }
}

