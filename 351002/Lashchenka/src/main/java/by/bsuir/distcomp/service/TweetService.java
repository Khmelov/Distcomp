package by.bsuir.distcomp.service;

import by.bsuir.distcomp.dto.request.TweetRequestTo;
import by.bsuir.distcomp.dto.response.TweetResponseTo;
import by.bsuir.distcomp.entity.Tweet;
import by.bsuir.distcomp.exception.DuplicateException;
import by.bsuir.distcomp.exception.ResourceNotFoundException;
import by.bsuir.distcomp.mapper.TweetMapper;
import by.bsuir.distcomp.repository.impl.InMemoryEditorRepository;
import by.bsuir.distcomp.repository.impl.InMemoryTweetRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class TweetService {

    private final InMemoryTweetRepository tweetRepository;
    private final InMemoryEditorRepository editorRepository;
    private final TweetMapper tweetMapper;

    public TweetService(InMemoryTweetRepository tweetRepository, 
                        InMemoryEditorRepository editorRepository,
                        TweetMapper tweetMapper) {
        this.tweetRepository = tweetRepository;
        this.editorRepository = editorRepository;
        this.tweetMapper = tweetMapper;
    }

    public TweetResponseTo create(TweetRequestTo dto) {
        if (!editorRepository.existsById(dto.getEditorId())) {
            throw new ResourceNotFoundException("Editor with id " + dto.getEditorId() + " not found", 40404);
        }
        if (tweetRepository.existsByTitle(dto.getTitle())) {
            throw new DuplicateException("Tweet with title '" + dto.getTitle() + "' already exists", 40303);
        }
        Tweet entity = tweetMapper.toEntity(dto);
        entity.setCreated(LocalDateTime.now());
        entity.setModified(LocalDateTime.now());
        Tweet saved = tweetRepository.save(entity);
        return tweetMapper.toResponseDto(saved);
    }

    public TweetResponseTo getById(Long id) {
        Tweet entity = tweetRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Tweet with id " + id + " not found", 40405));
        return tweetMapper.toResponseDto(entity);
    }

    public List<TweetResponseTo> getAll() {
        return tweetRepository.findAll().stream()
                .map(tweetMapper::toResponseDto)
                .collect(Collectors.toList());
    }

    public TweetResponseTo update(TweetRequestTo dto) {
        Tweet existingTweet = tweetRepository.findById(dto.getId())
                .orElseThrow(() -> new ResourceNotFoundException("Tweet with id " + dto.getId() + " not found", 40406));
        if (!editorRepository.existsById(dto.getEditorId())) {
            throw new ResourceNotFoundException("Editor with id " + dto.getEditorId() + " not found", 40407);
        }
        if (tweetRepository.existsByTitleAndIdNot(dto.getTitle(), dto.getId())) {
            throw new DuplicateException("Tweet with title '" + dto.getTitle() + "' already exists", 40304);
        }
        Tweet entity = tweetMapper.toEntity(dto);
        entity.setCreated(existingTweet.getCreated());
        entity.setModified(LocalDateTime.now());
        Tweet updated = tweetRepository.update(entity);
        return tweetMapper.toResponseDto(updated);
    }

    public void deleteById(Long id) {
        if (!tweetRepository.existsById(id)) {
            throw new ResourceNotFoundException("Tweet with id " + id + " not found", 40408);
        }
        tweetRepository.deleteById(id);
    }
}
