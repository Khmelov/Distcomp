package by.bsuir.task330.publisher.service.impl;

import by.bsuir.task330.publisher.domain.Creator;
import by.bsuir.task330.publisher.dto.request.CreatorRequestTo;
import by.bsuir.task330.publisher.dto.response.CreatorResponseTo;
import by.bsuir.task330.publisher.mapper.CreatorMapper;
import by.bsuir.task330.publisher.repository.CreatorRepository;
import by.bsuir.task330.publisher.repository.specification.CreatorSpecifications;
import by.bsuir.task330.publisher.service.CreatorService;
import by.bsuir.task330.publisher.error.NotFoundException;
import by.bsuir.task330.publisher.error.ValidationException;
import org.springframework.stereotype.Service;
import by.bsuir.task330.publisher.error.AlreadyExistsException;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional
public class CreatorServiceImpl implements CreatorService {

    private final CreatorRepository repository;
    private final CreatorMapper mapper;
    private final PageableFactory pageableFactory;

    public CreatorServiceImpl(CreatorRepository repository,
                              CreatorMapper mapper,
                              PageableFactory pageableFactory) {
        this.repository = repository;
        this.mapper = mapper;
        this.pageableFactory = pageableFactory;
    }

    @Override
    public CreatorResponseTo create(CreatorRequestTo request) {
        repository.findByLogin(request.getLogin())
                .ifPresent(c -> {
                    throw new AlreadyExistsException("Creator login already exists");
                });

        Creator creator = mapper.toEntity(request);
        creator.setId(null);

        Creator saved = repository.saveAndFlush(creator);
        return mapper.toResponse(saved);
    }

    @Override
    public CreatorResponseTo update(CreatorRequestTo request) {
        if (request.getId() == null) {
            throw new ValidationException("id is required");
        }

        Creator entity = repository.findById(request.getId())
                .orElseThrow(() -> new NotFoundException("Creator not found"));

        entity.setLogin(request.getLogin());
        entity.setPassword(request.getPassword());
        entity.setFirstname(request.getFirstname());
        entity.setLastname(request.getLastname());

        Creator updated = repository.saveAndFlush(entity);
        return mapper.toResponse(updated);
    }

    @Override
    @Transactional(readOnly = true)
    public CreatorResponseTo findById(Long id) {
        return mapper.toResponse(requireEntity(id));
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
    }

    @Override
    @Transactional(readOnly = true)
    public Creator requireEntity(Long id) {
        return repository.findById(id)
                .orElseThrow(() -> new NotFoundException("Creator not found"));
    }
}
