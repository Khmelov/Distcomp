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

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class PostService {
    private final PostRepository repository;
    private final NewsRepository newsRepository;
    private final PostMapper mapper;

    public PostResponseTo create(PostRequestTo request) {
        if (!newsRepository.existsById(request.getNewsId())) {
            throw new EntityNotFoundException("News not found");
        }
        Post entity = mapper.toEntity(request);
        entity.setId(System.currentTimeMillis());
        return mapper.toResponse(repository.save(entity));
    }

    public List<PostResponseTo> findAll(int page, int size, String sortBy) {
        return repository.findAll() // Берем все записи без PageRequest
                .stream()
                .map(mapper::toResponse)
                .collect(Collectors.toList());
    }

    public PostResponseTo findById(Long id) {
        return repository.findById(id)
                .map(mapper::toResponse)
                .orElseThrow(() -> new EntityNotFoundException("Post not found"));
    }

    public PostResponseTo update(PostRequestTo request) {
        // 1. Убираем проверку newsRepository.existsById.

        // 2. Создаем сущность напрямую
        Post entity = mapper.toEntity(request);

        // 3. Явно проставляем ID, чтобы Cassandra знала, что это UPDATE, а не INSERT
        entity.setId(request.getId());
        entity.setNewsId(request.getNewsId());

        // 4. Сохраняем без лишних try-catch (пусть ошибка вылетит в консоль, если она есть)
        Post saved = repository.save(entity);
        return mapper.toResponse(saved);
    }

    public void delete(Long id) {
        if (!repository.existsById(id)) {
            throw new EntityNotFoundException("Post not found");
        }
        repository.deleteById(id);
    }
}