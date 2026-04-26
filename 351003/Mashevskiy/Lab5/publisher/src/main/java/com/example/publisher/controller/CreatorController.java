package com.example.publisher.controller;

import com.example.publisher.entity.Creator;
import com.example.publisher.repository.CreatorRepository;
import com.example.publisher.service.CacheService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

@RestController
@RequestMapping("/api/v1.0/creators")
public class CreatorController {

    @Autowired
    private CreatorRepository creatorRepository;

    @Autowired
    private CacheService cacheService;

    private static final String CACHE_KEY_PREFIX = "creator:";

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public Creator createCreator(@Valid @RequestBody Creator creator) {
        if (creatorRepository.findByLogin(creator.getLogin()).isPresent()) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Login already exists");
        }
        Creator saved = creatorRepository.save(creator);
        cacheService.put(CACHE_KEY_PREFIX + saved.getId(), saved);
        return saved;
    }

    @GetMapping
    public Iterable<Creator> getAllCreators() {
        return creatorRepository.findAll();
    }

    @GetMapping("/{id}")
    public Creator getCreator(@PathVariable Long id) {
        String cacheKey = CACHE_KEY_PREFIX + id;
        Creator cached = (Creator) cacheService.get(cacheKey);
        if (cached != null) {
            return cached;
        }

        Creator creator = creatorRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));

        cacheService.put(cacheKey, creator);
        return creator;
    }

    @PutMapping("/{id}")
    public Creator updateCreator(@PathVariable Long id, @Valid @RequestBody Creator creator) {
        if (!creatorRepository.existsById(id)) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND);
        }
        creator.setId(id);
        Creator updated = creatorRepository.save(creator);

        cacheService.put(CACHE_KEY_PREFIX + id, updated);
        return updated;
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteCreator(@PathVariable Long id) {
        creatorRepository.deleteById(id);
        cacheService.evict(CACHE_KEY_PREFIX + id);
    }
}