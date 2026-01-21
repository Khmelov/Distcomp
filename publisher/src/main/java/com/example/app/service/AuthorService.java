package com.example.app.service;

import com.example.app.dto.AuthorRequestDTO;
import com.example.app.dto.AuthorResponseDTO;
import com.example.app.dto.cache.CachedAuthorDTO;
import com.example.app.exception.AppException;
import com.example.app.model.Author;
import com.example.app.repository.AuthorRepository;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.cache.annotation.Caching;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class AuthorService {
    private final AuthorRepository authorRepo;
    private final RedisCacheService cacheService;
    
    public AuthorService(AuthorRepository authorRepo, RedisCacheService cacheService) {
        this.authorRepo = authorRepo;
        this.cacheService = cacheService;
    }
    
    @Cacheable(value = "authors")
    public List<AuthorResponseDTO> getAllAuthors() {
        return authorRepo.findAll().stream()
                .map(this::toResponse)
                .toList();
    }
    
    @Cacheable(value = "authors", key = "#id")
    public AuthorResponseDTO getAuthorById(@NotNull Long id) {
        // Сначала проверяем кеш
        CachedAuthorDTO cached = cacheService.getCachedAuthor(id);
        if (cached != null) {
            return convertFromCache(cached);
        }
        
        // Если нет в кеше, загружаем из БД
        Author author = authorRepo.findById(id)
                .orElseThrow(() -> new AppException("Author not found", 40401));
        
        AuthorResponseDTO response = toResponse(author);
        
        // Кешируем
        cacheService.cacheAuthor(id, convertToCache(author));
        
        return response;
    }
    
    @Transactional
    @Caching(evict = {
        @CacheEvict(value = "authors", key = "#request.id()"),
        @CacheEvict(value = "authors", allEntries = true)
    })
    public AuthorResponseDTO createAuthor(@Valid AuthorRequestDTO request) {
        Author author = toEntity(request);
        Author saved = authorRepo.save(author);
        
        // Очищаем кеш
        cacheService.evictAllAuthorsCache();
        
        return toResponse(saved);
    }
    
    @Transactional
    @Caching(evict = {
        @CacheEvict(value = "authors", key = "#request.id()"),
        @CacheEvict(value = "authors", allEntries = true)
    })
    public AuthorResponseDTO updateAuthor(@Valid AuthorRequestDTO request) {
        if (request.id() == null) {
            throw new AppException("ID required for update", 40001);
        }
        
        Author existingAuthor = authorRepo.findById(request.id())
                .orElseThrow(() -> new AppException("Author not found for update", 40401));
        
        existingAuthor.setLogin(request.login());
        existingAuthor.setPassword(request.password());
        existingAuthor.setFirstname(request.firstname());
        existingAuthor.setLastname(request.lastname());
        
        Author updated = authorRepo.save(existingAuthor);
        
        // Инвалидируем кеш
        cacheService.evictAuthorCache(request.id());
        cacheService.evictPattern("tweet:*"); // Твиты этого автора тоже нужно обновить
        
        return toResponse(updated);
    }
    
    @Transactional
    @CacheEvict(value = "authors", key = "#id")
    public void deleteAuthor(@NotNull Long id) {
        if (!authorRepo.existsById(id)) {
            throw new AppException("Author not found for deletion", 40401);
        }
        
        authorRepo.deleteById(id);
        
        // Очищаем связанные данные
        cacheService.evictAuthorCache(id);
        cacheService.evictPattern("tweet:*author:" + id + "*");
        cacheService.evictPattern("tweet:*");
    }
    
    private Author toEntity(AuthorRequestDTO dto) {
        Author author = new Author();
        author.setId(dto.id());
        author.setLogin(dto.login());
        author.setPassword(dto.password());
        author.setFirstname(dto.firstname());
        author.setLastname(dto.lastname());
        return author;
    }
    
    private AuthorResponseDTO toResponse(Author author) {
        return new AuthorResponseDTO(
                author.getId(),
                author.getLogin(),
                author.getPassword(),
                author.getFirstname(),
                author.getLastname()
        );
    }
    
    private CachedAuthorDTO convertToCache(Author author) {
        return new CachedAuthorDTO(
            author.getId(),
            author.getLogin(),
            author.getPassword(),
            author.getFirstname(),
            author.getLastname()
        );
    }
    
    private AuthorResponseDTO convertFromCache(CachedAuthorDTO cached) {
        return new AuthorResponseDTO(
            cached.getId(),
            cached.getLogin(),
            cached.getPassword(),
            cached.getFirstname(),
            cached.getLastname()
        );
    }
}