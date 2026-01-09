package org.example.task310rest.service.impl;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import org.example.task310rest.dto.LabelResponseTo;
import org.example.task310rest.dto.MessageResponseTo;
import org.example.task310rest.dto.TweetRequestTo;
import org.example.task310rest.dto.TweetResponseTo;
import org.example.task310rest.dto.WriterResponseTo;
import org.example.task310rest.exception.ConflictException;
import org.example.task310rest.exception.NotFoundException;
import org.example.task310rest.exception.ValidationException;
import org.example.task310rest.mapper.LabelMapper;
import org.example.task310rest.mapper.MessageMapper;
import org.example.task310rest.mapper.TweetMapper;
import org.example.task310rest.mapper.WriterMapper;
import org.example.task310rest.model.Label;
import org.example.task310rest.model.Message;
import org.example.task310rest.model.Tweet;
import org.example.task310rest.model.Writer;
import org.example.task310rest.repository.LabelRepository;
import org.example.task310rest.repository.MessageRepository;
import org.example.task310rest.repository.TweetRepository;
import org.example.task310rest.repository.WriterRepository;
import org.example.task310rest.service.TweetService;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

@Service
public class TweetServiceImpl implements TweetService {

    private final TweetRepository tweetRepository;
    private final WriterRepository writerRepository;
    private final LabelRepository labelRepository;
    private final MessageRepository messageRepository;
    private final TweetMapper tweetMapper;
    private final WriterMapper writerMapper;
    private final LabelMapper labelMapper;
    private final MessageMapper messageMapper;

    public TweetServiceImpl(TweetRepository tweetRepository,
                            WriterRepository writerRepository,
                            LabelRepository labelRepository,
                            MessageRepository messageRepository,
                            TweetMapper tweetMapper,
                            WriterMapper writerMapper,
                            LabelMapper labelMapper,
                            MessageMapper messageMapper) {
        this.tweetRepository = tweetRepository;
        this.writerRepository = writerRepository;
        this.labelRepository = labelRepository;
        this.messageRepository = messageRepository;
        this.tweetMapper = tweetMapper;
        this.writerMapper = writerMapper;
        this.labelMapper = labelMapper;
        this.messageMapper = messageMapper;
    }

    @Override
    public TweetResponseTo create(TweetRequestTo request) {
        validate(request);
        
        // Check writer exists
        writerRepository.findById(request.getWriterId())
                .orElseThrow(() -> new NotFoundException("Writer not found: " + request.getWriterId()));
        
        // Check for duplicate title
        if (tweetRepository.findByTitle(request.getTitle()).isPresent()) {
            throw new ConflictException("Tweet with title already exists: " + request.getTitle());
        }
        
        Tweet entity = tweetMapper.toEntity(request);
        entity.setCreated(OffsetDateTime.now());
        entity.setModified(entity.getCreated());
        
        tweetRepository.save(entity);
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
    public TweetResponseTo update(Long id, TweetRequestTo request) {
        validate(request);
        Tweet entity = tweetRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Tweet not found: " + id));
        
        writerRepository.findById(request.getWriterId())
                .orElseThrow(() -> new NotFoundException("Writer not found: " + request.getWriterId()));
        
        // Check for duplicate title if title changed
        if (!entity.getTitle().equals(request.getTitle())) {
            if (tweetRepository.findByTitle(request.getTitle()).isPresent()) {
                throw new ConflictException("Tweet with title already exists: " + request.getTitle());
            }
        }
        
        tweetMapper.updateEntityFromDto(request, entity);
        entity.setModified(OffsetDateTime.now());
        
        tweetRepository.save(entity);
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
        Writer writer = writerRepository.findById(tweet.getWriterId())
                .orElseThrow(() -> new NotFoundException("Writer not found: " + tweet.getWriterId()));
        return writerMapper.toDto(writer);
    }

    @Override
    public List<LabelResponseTo> getLabelsByTweetId(Long tweetId) {
        Tweet tweet = tweetRepository.findById(tweetId)
                .orElseThrow(() -> new NotFoundException("Tweet not found: " + tweetId));
        Set<Long> ids = tweet.getLabelIds();
        return labelRepository.findAll().stream()
                .filter(l -> ids.contains(l.getId()))
                .map(labelMapper::toDto)
                .collect(Collectors.toList());
    }

    @Override
    public List<MessageResponseTo> getMessagesByTweetId(Long tweetId) {
        tweetRepository.findById(tweetId)
                .orElseThrow(() -> new NotFoundException("Tweet not found: " + tweetId));
        return messageRepository.findByTweetId(tweetId).stream()
                .map(messageMapper::toDto)
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
