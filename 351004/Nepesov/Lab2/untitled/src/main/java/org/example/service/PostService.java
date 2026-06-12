package org.example.service;

import lombok.RequiredArgsConstructor;
import org.example.dto.PostRequestTo;
import org.example.dto.PostResponseTo;
import org.example.exception.EntityNotFoundException;
import org.example.mapper.PostMapper;
import org.example.model.Post;
import org.example.repository.NewsRepository;
import org.example.repository.PostRepository;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
public class PostService {
    private final PostRepository repository;
    private final NewsRepository newsRepository;
    private final PostMapper mapper;

    public PostResponseTo create(PostRequestTo request) {
        if (!newsRepository.existsById(request.getNewsId())) {
            throw new EntityNotFoundException("News not found");
        }
        Post entity = mapper.toEntity(request);
        return mapper.toResponse(repository.save(entity));
    }

    @Transactional(readOnly = true)
    public List<PostResponseTo> findAll(int page, int size, String sortBy) {
        return repository.findAll(PageRequest.of(page, size, Sort.by(sortBy)))
                .stream()
                .map(mapper::toResponse)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public PostResponseTo findById(Long id) {
        return repository.findById(id)
                .map(mapper::toResponse)
                .orElseThrow(() -> new EntityNotFoundException("Post not found"));
    }

    public PostResponseTo update(PostRequestTo request) {
        if (!repository.existsById(request.getId())) {
            throw new EntityNotFoundException("Post not found");
        }
        if (!newsRepository.existsById(request.getNewsId())) {
            throw new EntityNotFoundException("News not found");
        }
        return mapper.toResponse(repository.save(mapper.toEntity(request)));
    }

    public void delete(Long id) {
        if (!repository.existsById(id)) {
            throw new EntityNotFoundException("Post not found");
        }
        repository.deleteById(id);
    }
}