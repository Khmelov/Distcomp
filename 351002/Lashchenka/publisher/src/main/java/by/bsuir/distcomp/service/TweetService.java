package by.bsuir.distcomp.service;

import by.bsuir.distcomp.dto.request.TweetRequestTo;
import by.bsuir.distcomp.dto.response.TweetResponseTo;
import by.bsuir.distcomp.entity.Tweet;
import by.bsuir.distcomp.exception.DuplicateException;
import by.bsuir.distcomp.exception.ResourceNotFoundException;
import by.bsuir.distcomp.mapper.TweetMapper;
import by.bsuir.distcomp.repository.EditorRepository;
import by.bsuir.distcomp.repository.TweetRepository;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.cache.annotation.Caching;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional
public class TweetService {

    private final TweetRepository tweetRepository;
    private final EditorRepository editorRepository;
    private final TweetMapper tweetMapper;

    public TweetService(TweetRepository tweetRepository,
                        EditorRepository editorRepository,
                        TweetMapper tweetMapper) {
        this.tweetRepository = tweetRepository;
        this.editorRepository = editorRepository;
        this.tweetMapper = tweetMapper;
    }

    @Caching(put = @CachePut(value = "tweets", key = "#result.id"),
            evict = @CacheEvict(value = "tweets", key = "'all'"))
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

    @Cacheable(value = "tweets", key = "#id")
    @Transactional(readOnly = true)
    public TweetResponseTo getById(Long id) {
        Tweet entity = tweetRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Tweet with id " + id + " not found", 40405));
        return tweetMapper.toResponseDto(entity);
    }

    @Cacheable(value = "tweets", key = "'all'")
    @Transactional(readOnly = true)
    public List<TweetResponseTo> getAll() {
        return tweetRepository.findAll().stream()
                .map(tweetMapper::toResponseDto)
                .collect(Collectors.toList());
    }

    @Caching(put = @CachePut(value = "tweets", key = "#result.id"),
            evict = @CacheEvict(value = "tweets", key = "'all'"))
    public TweetResponseTo update(TweetRequestTo dto) {
        Tweet existing = tweetRepository.findById(dto.getId())
                .orElseThrow(() -> new ResourceNotFoundException("Tweet with id " + dto.getId() + " not found", 40406));
        if (!editorRepository.existsById(dto.getEditorId())) {
            throw new ResourceNotFoundException("Editor with id " + dto.getEditorId() + " not found", 40407);
        }
        if (tweetRepository.existsByTitleAndIdNot(dto.getTitle(), dto.getId())) {
            throw new DuplicateException("Tweet with title '" + dto.getTitle() + "' already exists", 40304);
        }
        LocalDateTime originalCreated = existing.getCreated();
        tweetMapper.updateEntityFromDto(dto, existing);
        existing.setCreated(originalCreated);
        existing.setModified(LocalDateTime.now());
        Tweet updated = tweetRepository.save(existing);
        return tweetMapper.toResponseDto(updated);
    }

    @Caching(evict = {
            @CacheEvict(value = "tweets", key = "#id"),
            @CacheEvict(value = "tweets", key = "'all'")
    })
    public void deleteById(Long id) {
        if (!tweetRepository.existsById(id)) {
            throw new ResourceNotFoundException("Tweet with id " + id + " not found", 40408);
        }
        tweetRepository.deleteById(id);
    }
}
