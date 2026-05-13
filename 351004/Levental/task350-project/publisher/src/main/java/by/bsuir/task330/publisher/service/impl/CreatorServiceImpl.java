package by.bsuir.task330.publisher.service.impl;

import by.bsuir.task330.publisher.cache.RedisCacheService;
import by.bsuir.task330.publisher.domain.Creator;
import by.bsuir.task330.publisher.dto.request.CreatorRequestTo;
import by.bsuir.task330.publisher.dto.response.CreatorResponseTo;
import by.bsuir.task330.publisher.error.AlreadyExistsException;
import by.bsuir.task330.publisher.error.NotFoundException;
import by.bsuir.task330.publisher.error.ValidationException;
import by.bsuir.task330.publisher.mapper.CreatorMapper;
import by.bsuir.task330.publisher.repository.CreatorRepository;
import by.bsuir.task330.publisher.repository.specification.CreatorSpecifications;
import by.bsuir.task330.publisher.service.CreatorService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional
public class CreatorServiceImpl implements CreatorService {

    private static final String CACHE_PREFIX = "creator:";
    private static final String CACHE_LIST_PREFIX = "creator:list:";

    private final CreatorRepository repository;
    private final CreatorMapper mapper;
    private final PageableFactory pageableFactory;
    private final RedisCacheService cache;

    public CreatorServiceImpl(CreatorRepository repository,
                              CreatorMapper mapper,
                              PageableFactory pageableFactory,
                              RedisCacheService cache) {
        this.repository = repository;
        this.mapper = mapper;
        this.pageableFactory = pageableFactory;
        this.cache = cache;
    }

    @Override
    public CreatorResponseTo create(CreatorRequestTo request) {
        repository.findByLogin(request.getLogin())
                .ifPresent(c -> { throw new AlreadyExistsException("Creator login already exists"); });

        Creator creator = mapper.toEntity(request);
        creator.setId(null);

        Creator saved = repository.saveAndFlush(creator);
        CreatorResponseTo response = mapper.toResponse(saved);
        cache.save(CACHE_PREFIX + saved.getId(), response);
        cache.deleteByPrefix(CACHE_LIST_PREFIX);
        return response;
    }

    @Override
    public CreatorResponseTo update(Long id, CreatorRequestTo request) {

        Creator entity = repository.findById(id)
                .orElseThrow(() -> new NotFoundException("Creator not found"));

        entity.setLogin(request.getLogin());
        entity.setPassword(request.getPassword());
        entity.setFirstname(request.getFirstname());
        entity.setLastname(request.getLastname());

        Creator updated = repository.saveAndFlush(entity);

        CreatorResponseTo response = mapper.toResponse(updated);

        cache.save(CACHE_PREFIX + id, response);

        cache.deleteByPrefix(CACHE_LIST_PREFIX);

        return response;
    }
    @Override
    @Transactional(readOnly = true)
    public CreatorResponseTo findById(Long id) {
        String key = CACHE_PREFIX + id;
        CreatorResponseTo cached = cache.get(key, CreatorResponseTo.class);
        if (cached != null) {
            return cached;
        }

        CreatorResponseTo response = mapper.toResponse(requireEntity(id));
        cache.save(key, response);
        return response;
    }

    @Override
    @Transactional(readOnly = true)
    public List<CreatorResponseTo> findAll(Integer page, Integer size, String sort, String filter) {

        var pageable = pageableFactory.create(page, size, sort, "id");

        return repository.findAll(CreatorSpecifications.byFilter(filter), pageable)
                .getContent()
                .stream()
                .map(mapper::toResponse)
                .toList();
    }

    @Override
    public void delete(Long id) {
        Creator entity = repository.findById(id)
                .orElseThrow(() -> new NotFoundException("Creator not found"));
        repository.delete(entity);
        repository.flush();
        cache.delete(CACHE_PREFIX + id);
        cache.deleteByPrefix(CACHE_LIST_PREFIX);
    }

    @Override
    @Transactional(readOnly = true)
    public Creator requireEntity(Long id) {
        return repository.findById(id)
                .orElseThrow(() -> new NotFoundException("Creator not found"));
    }
}
