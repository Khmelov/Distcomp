package com.example.restservice.controller;

import com.example.restservice.dto.AuthorCreateDto;
import com.example.restservice.dto.AuthorResponseDto;
import com.example.restservice.mapper.AuthorMapper;
import com.example.restservice.model.Author;
import com.example.restservice.repository.AuthorRepository;
import com.example.restservice.service.AuthorService;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/v1.0")
@RequiredArgsConstructor
public class AuthorController {

    private final AuthorMapper authorMapper;
    private final AuthorRepository authorRepository;
    private final AuthorService authorService;

    private final RedisTemplate<String, Object> redisTemplate;
    ObjectMapper objectMapper = new ObjectMapper();
    private static final String CACHE_NAME = "authors";
    private static final long CACHE_TTL_MINUTES = 30;

    /*@Autowired
    public AuthorController(AuthorMapper authorMapper, AuthorRepository authorRepository, AuthorService authorService) {
        this.authorMapper = authorMapper;
        this.authorRepository = authorRepository;
        this.authorService = authorService;
    }*/
    @GetMapping("/authors")
    @ResponseStatus(HttpStatus.OK)
    //@Cacheable(value = CACHE_NAME, key = "'allAuthors'")
    public List<AuthorResponseDto> getAuthors() {
        String key = "allAuthors";
        Object cached = redisTemplate.opsForValue().get(key);
        if (cached != null) {
            return objectMapper.convertValue(cached, new TypeReference<List<AuthorResponseDto>>() {});
        }

        List<AuthorResponseDto> authors = authorService.findAll().stream()
                .map(authorMapper::toDto)
                .collect(Collectors.toList());

        redisTemplate.opsForValue().set(key, authors, CACHE_TTL_MINUTES, TimeUnit.MINUTES);
        return authors;
    }

    @PostMapping("/authors")
    @ResponseStatus(HttpStatus.CREATED)
    public ResponseEntity<AuthorResponseDto> createAuthor(@RequestBody @Valid AuthorCreateDto inputDto) {
        Author entity = authorMapper.toEntity(inputDto);
        if (authorRepository.existsByLogin(entity.getLogin())) {
            return new ResponseEntity<>(authorMapper.toDto(entity), HttpStatus.FORBIDDEN);
        }
        Author saved = authorService.save(entity);
        redisTemplate.delete("allAuthors");
        redisTemplate.opsForValue().set(
                "author:" + inputDto.getId(),
                authorMapper.toDto(saved),
                CACHE_TTL_MINUTES,
                TimeUnit.MINUTES
        );
        return new ResponseEntity<>(authorMapper.toDto(saved), HttpStatus.CREATED);
    }

    @GetMapping("/authors/{id}")
    public AuthorResponseDto getAuthorById(@PathVariable Long id) {
        String key = "author:" + id;
        Object cached = redisTemplate.opsForValue().get(key);

        if (cached != null) {
            return objectMapper.convertValue(cached, AuthorResponseDto.class);
        }
        return authorMapper.toDto(authorService.findById(id));
    }

    @PutMapping("/authors")
    public ResponseEntity<AuthorResponseDto> updateAuthor(@RequestBody @Valid AuthorCreateDto in) {
        redisTemplate.delete("allAuthors");
        redisTemplate.delete("author:" + in.getId());
        redisTemplate.opsForValue().set(
                "author:" + in.getId(),
                authorMapper.toDto(authorMapper.toEntity(in)),
                CACHE_TTL_MINUTES,
                TimeUnit.MINUTES
        );

        Author author = authorService.findById(in.getId());
        if (author == null) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        } else {
            Author newAuthor = authorMapper.toEntity(in);
            Author updated = authorService.update(newAuthor);
            return ResponseEntity.ok(authorMapper.toDto(updated));
        }
    }

    @DeleteMapping("/authors/{id}")
    public ResponseEntity<Void> deleteAuthor(@PathVariable Long id) {
        redisTemplate.delete("allAuthors");
        redisTemplate.delete("author:" + id);

        if (authorService.findById(id) != null) {
            authorService.deleteById(id);
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.notFound().build();
    }
}
