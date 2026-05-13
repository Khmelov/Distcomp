package by.bsuir.distcomp.service;

import by.bsuir.distcomp.dto.WriterDto;
import by.bsuir.distcomp.exception.ApiException;
import by.bsuir.distcomp.cache.RedisCacheService;
import by.bsuir.distcomp.model.Writer;
import by.bsuir.distcomp.repository.WriterRepository;
import java.util.List;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class WriterService implements CrudService<WriterDto> {
    private final WriterRepository repository;
    private final RedisCacheService cache;

    public WriterService(WriterRepository repository, RedisCacheService cache) {
        this.repository = repository;
        this.cache = cache;
    }

    @Override
    public WriterDto create(WriterDto dto) {
        if (repository.existsByLogin(dto.login())) {
            throw new ApiException(HttpStatus.FORBIDDEN, "40301", "Writer login already exists");
        }
        WriterDto created = toDto(repository.save(toEntity(new Writer(), dto)));
        cache.put(key(created.id()), created);
        cache.evictByPrefix(listPrefix());
        return created;
    }

    @Override
    @Transactional(readOnly = true)
    public WriterDto get(Long id) {
        WriterDto cached = cache.get(key(id), WriterDto.class);
        if (cached != null) {
            return cached;
        }
        WriterDto dto = toDto(findEntity(id));
        cache.put(key(id), dto);
        return dto;
    }

    @Override
    @Transactional(readOnly = true)
    public List<WriterDto> findAll(Pageable pageable) {
        String key = listKey(pageable);
        List<WriterDto> cached = cache.getList(key, WriterDto.class);
        if (cached != null) {
            return cached;
        }
        List<WriterDto> writers = repository.findAll(pageable).stream().map(this::toDto).toList();
        cache.put(key, writers);
        return writers;
    }

    @Override
    public WriterDto update(Long id, WriterDto dto) {
        Writer writer = findEntity(id);
        if (repository.existsByLoginAndIdNot(dto.login(), id)) {
            throw new ApiException(HttpStatus.FORBIDDEN, "40301", "Writer login already exists");
        }
        WriterDto updated = toDto(repository.save(toEntity(writer, dto)));
        cache.put(key(id), updated);
        cache.evictByPrefix(listPrefix());
        return updated;
    }

    @Override
    public void delete(Long id) {
        Writer writer = findEntity(id);
        repository.delete(writer);
        cache.evict(key(id));
        cache.evictByPrefix(listPrefix());
    }

    private Writer findEntity(Long id) {
        if (id == null) {
            throw new ApiException(HttpStatus.BAD_REQUEST, "40004", "Writer id is required");
        }
        return repository.findById(id)
                .orElseThrow(() -> new ApiException(HttpStatus.NOT_FOUND, "40401", "Writer not found"));
    }

    private Writer toEntity(Writer writer, WriterDto dto) {
        writer.setLogin(dto.login());
        writer.setPassword(dto.password());
        writer.setFirstname(dto.firstname());
        writer.setLastname(dto.lastname());
        writer.setRole(dto.role());
        return writer;
    }

    private WriterDto toDto(Writer writer) {
        return new WriterDto(writer.getId(), writer.getLogin(), writer.getPassword(),
                writer.getFirstname(), writer.getLastname(), writer.getRole());
    }

    private String key(Long id) {
        return "writer:" + id;
    }

    private String listPrefix() {
        return "writer:list:";
    }

    private String listKey(Pageable pageable) {
        return listPrefix() + pageable.getPageNumber() + ":" + pageable.getPageSize();
    }
}
