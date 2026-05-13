package by.bsuir.task330.publisher.service.impl;

import by.bsuir.task330.publisher.domain.Sticker;
import by.bsuir.task330.publisher.dto.request.StickerRequestTo;
import by.bsuir.task330.publisher.dto.response.StickerResponseTo;
import by.bsuir.task330.publisher.mapper.StickerMapper;
import by.bsuir.task330.publisher.repository.StickerRepository;
import by.bsuir.task330.publisher.repository.specification.StickerSpecifications;
import by.bsuir.task330.publisher.service.StickerService;
import by.bsuir.task330.publisher.error.AlreadyExistsException;
import by.bsuir.task330.publisher.error.NotFoundException;
import by.bsuir.task330.publisher.error.ValidationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Service
@Transactional
public class StickerServiceImpl implements StickerService {

    private final StickerRepository repository;
    private final StickerMapper mapper;
    private final PageableFactory pageableFactory;

    public StickerServiceImpl(StickerRepository repository,
                              StickerMapper mapper,
                              PageableFactory pageableFactory) {
        this.repository = repository;
        this.mapper = mapper;
        this.pageableFactory = pageableFactory;
    }

    @Override
    public StickerResponseTo create(StickerRequestTo request) {
        repository.findByName(request.name())
                .ifPresent(existing -> {
                    throw new AlreadyExistsException("Sticker name already exists");
                });

        Sticker sticker = mapper.toEntity(request);
        sticker.setId(null);

        Sticker saved = repository.saveAndFlush(sticker);
        return mapper.toResponse(saved);
    }

    @Override
    public StickerResponseTo update(StickerRequestTo request) {
        if (request.id() == null) {
            throw new ValidationException("id is required");
        }

        Sticker sticker = requireEntity(request.id());

        repository.findByName(request.name())
                .filter(existing -> !existing.getId().equals(request.id()))
                .ifPresent(existing -> {
                    throw new AlreadyExistsException("Sticker name already exists");
                });

        mapper.update(request, sticker);

        Sticker updated = repository.saveAndFlush(sticker);
        return mapper.toResponse(updated);
    }

    @Override
    @Transactional(readOnly = true)
    public StickerResponseTo findById(Long id) {
        return mapper.toResponse(requireEntity(id));
    }

    @Override
    @Transactional(readOnly = true)
    public List<StickerResponseTo> findAll(Integer page, Integer size, String sort, String filter) {
        var pageable = pageableFactory.create(page, size, sort, "id");
        return repository.findAll(StickerSpecifications.byFilter(filter), pageable)
                .getContent()
                .stream()
                .map(mapper::toResponse)
                .toList();
    }

    @Override
    public void delete(Long id) {
        Sticker sticker = requireEntity(id);
        repository.delete(sticker);
        repository.flush();
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
