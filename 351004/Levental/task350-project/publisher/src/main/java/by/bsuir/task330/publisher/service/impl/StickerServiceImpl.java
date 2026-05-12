package by.bsuir.task330.publisher.service.impl;

import by.bsuir.task330.publisher.cache.RedisCacheService;
import by.bsuir.task330.publisher.domain.Sticker;
import by.bsuir.task330.publisher.dto.request.StickerRequestTo;
import by.bsuir.task330.publisher.dto.response.StickerResponseTo;
import by.bsuir.task330.publisher.error.AlreadyExistsException;
import by.bsuir.task330.publisher.error.NotFoundException;
import by.bsuir.task330.publisher.error.ValidationException;
import by.bsuir.task330.publisher.mapper.StickerMapper;
import by.bsuir.task330.publisher.repository.StickerRepository;
import by.bsuir.task330.publisher.repository.specification.StickerSpecifications;
import by.bsuir.task330.publisher.service.StickerService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Service
@Transactional
public class StickerServiceImpl implements StickerService {

    private static final String CACHE_PREFIX = "sticker:";
    private static final String CACHE_LIST_PREFIX = "sticker:list:";

    private final StickerRepository repository;
    private final StickerMapper mapper;
    private final PageableFactory pageableFactory;
    private final RedisCacheService cache;
    private final ObjectMapper objectMapper;

    public StickerServiceImpl(StickerRepository repository,
                              StickerMapper mapper,
                              PageableFactory pageableFactory,
                              RedisCacheService cache,
                              ObjectMapper objectMapper) {
        this.repository = repository;
        this.mapper = mapper;
        this.pageableFactory = pageableFactory;
        this.cache = cache;
        this.objectMapper = objectMapper;
    }

    @Override
    public StickerResponseTo create(StickerRequestTo request) {
        repository.findByName(request.name())
                .ifPresent(existing -> { throw new AlreadyExistsException("Sticker name already exists"); });

        Sticker sticker = mapper.toEntity(request);
        sticker.setId(null);

        Sticker saved = repository.saveAndFlush(sticker);
        StickerResponseTo response = mapper.toResponse(saved);
        cache.save(CACHE_PREFIX + saved.getId(), response);
        cache.deleteByPrefix(CACHE_LIST_PREFIX);
        return response;
    }

    @Override
    public StickerResponseTo update(StickerRequestTo request) {
        if (request.id() == null) {
            throw new ValidationException("id is required");
        }

        Sticker sticker = requireEntity(request.id());

        repository.findByName(request.name())
                .filter(existing -> !existing.getId().equals(request.id()))
                .ifPresent(existing -> { throw new AlreadyExistsException("Sticker name already exists"); });

        mapper.update(request, sticker);

        Sticker updated = repository.saveAndFlush(sticker);
        StickerResponseTo response = mapper.toResponse(updated);
        cache.save(CACHE_PREFIX + updated.getId(), response);
        cache.deleteByPrefix(CACHE_LIST_PREFIX);
        return response;
    }

    @Override
    @Transactional(readOnly = true)
    public StickerResponseTo findById(Long id) {
        String key = CACHE_PREFIX + id;
        StickerResponseTo cached = cache.get(key, StickerResponseTo.class);
        if (cached != null) {
            return cached;
        }

        StickerResponseTo response = mapper.toResponse(requireEntity(id));
        cache.save(key, response);
        return response;
    }

    @Override
    @Transactional(readOnly = true)
    public List<StickerResponseTo> findAll(Integer page, Integer size, String sort, String filter) {
        String key = CACHE_LIST_PREFIX + page + ":" + size + ":" + sort + ":" + filter;
        Object cached = cache.get(key);
        if (cached instanceof List<?> list) {
            return list.stream()
                    .map(item -> item instanceof StickerResponseTo dto ? dto : objectMapper.convertValue(item, StickerResponseTo.class))
                    .toList();
        }

        var pageable = pageableFactory.create(page, size, sort, "id");
        List<StickerResponseTo> response = repository.findAll(StickerSpecifications.byFilter(filter), pageable)
                .getContent()
                .stream()
                .map(mapper::toResponse)
                .toList();
        cache.save(key, response);
        return response;
    }

    @Override
    public void delete(Long id) {
        Sticker sticker = requireEntity(id);
        repository.delete(sticker);
        repository.flush();
        cache.delete(CACHE_PREFIX + id);
        cache.deleteByPrefix(CACHE_LIST_PREFIX);
    }

    @Override
    @Transactional(readOnly = true)
    public Sticker requireEntity(Long id) {
        return repository.findById(id)
                .orElseThrow(() -> new NotFoundException("Sticker not found"));
    }

    @Override
    @Transactional(readOnly = true)
    public Set<Sticker> requireEntities(Set<Long> ids) {
        if (ids == null || ids.isEmpty()) {
            return new HashSet<>();
        }

        Set<Sticker> stickers = new HashSet<>(repository.findAllById(ids));
        if (stickers.size() != ids.size()) {
            throw new NotFoundException("One or more stickers not found");
        }

        return stickers;
    }
}
