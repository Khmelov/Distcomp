package com.example.publisher.service;

import com.example.publisher.dto.request.AuthorRequestTo;
import com.example.publisher.dto.response.AuthorResponseTo;
import com.example.publisher.exception.EntityNotFoundException;
import com.example.publisher.mapper.AuthorMapper;
import com.example.publisher.model.Author;
import com.example.publisher.model.Role;
import com.example.publisher.model.Sticker;
import com.example.publisher.repository.AuthorRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.cache.annotation.Caching;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class AuthorService {
    private final AuthorRepository repository;
    private final AuthorMapper mapper;
    private final com.example.publisher.repository.StickerRepository stickerRepository;
    private final PasswordEncoder passwordEncoder;

    @Transactional
    @Caching(
            put = @CachePut(value = "author", key = "#result.id"),
            evict = @CacheEvict(value = "authors_list", allEntries = true)
    )
    public AuthorResponseTo create(AuthorRequestTo request) {
        Author author = mapper.toEntity(request);
        author.setPassword(passwordEncoder.encode(request.getPassword()));

        if (request.getRole() != null && !request.getRole().isEmpty()) {
            author.setRole(Role.valueOf(request.getRole().toUpperCase()));
        } else {
            author.setRole(Role.CUSTOMER); // Роль по умолчанию
        }

        Author saved = repository.save(author);
        return mapper.toResponse(saved);
    }

    @Cacheable(value = "authors_list")
    public List<AuthorResponseTo> getAll() {
        return repository.findAll().stream()
                .map(mapper::toResponse)
                .collect(Collectors.toList());
    }

    @Cacheable(value = "author", key = "#id")
    public AuthorResponseTo getById(Long id) {
        return repository.findById(id)
                .map(mapper::toResponse)
                .orElseThrow(() -> new EntityNotFoundException("Author not found with id: " + id));
    }

    @Transactional
    @Caching(
            put = @CachePut(value = "author", key = "#id"),
            evict = @CacheEvict(value = "authors_list", allEntries = true)
    )
    public AuthorResponseTo update(Long id, AuthorRequestTo request) {
        Author author = repository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Author not found with id: " + id));

        // 1. Сохраняем текущую роль перед маппингом
        Role oldRole = author.getRole();

        // 2. Маппер применяет новые данные (перезапишет роль на null, если в запросе её нет)
        mapper.updateEntityFromDto(request, author);

        // 3. Восстанавливаем роль, если она пришла пустой (обратная совместимость с v1.0)
        if (request.getRole() != null && !request.getRole().isEmpty()) {
            author.setRole(Role.valueOf(request.getRole().toUpperCase()));
        } else {
            author.setRole(oldRole); // Возвращаем старую
        }

        // 4. Шифруем пароль (Маппер перенес его открытым текстом, мы шифруем)
        if (request.getPassword() != null && !request.getPassword().isEmpty()) {
            author.setPassword(passwordEncoder.encode(request.getPassword()));
        }

        Author saved = repository.save(author);
        return mapper.toResponse(saved);
    }

    @Transactional
    @Caching(evict = {
            @CacheEvict(value = "author", key = "#id"),
            @CacheEvict(value = "authors_list", allEntries = true)
    })
    public void delete(Long id) {
        Author author = repository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Author not found with id: " + id));
        List<Sticker> stickersToDelete = author.getArticles().stream()
                .flatMap(article -> article.getStickers().stream())
                .collect(Collectors.toList());
        repository.delete(author);
        repository.flush();
        if (!stickersToDelete.isEmpty()) {
            stickerRepository.deleteAll(stickersToDelete);
            stickerRepository.flush();
        }
    }
}