package by.bsuir.distcomp.service;

import by.bsuir.distcomp.dto.IssueDto;
import by.bsuir.distcomp.exception.ApiException;
import by.bsuir.distcomp.cache.RedisCacheService;
import by.bsuir.distcomp.model.Issue;
import by.bsuir.distcomp.model.Writer;
import by.bsuir.distcomp.repository.IssueRepository;
import by.bsuir.distcomp.repository.WriterRepository;
import java.util.List;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class IssueService implements CrudService<IssueDto> {
    private final IssueRepository repository;
    private final WriterRepository writerRepository;
    private final RedisCacheService cache;

    public IssueService(IssueRepository repository, WriterRepository writerRepository, RedisCacheService cache) {
        this.repository = repository;
        this.writerRepository = writerRepository;
        this.cache = cache;
    }

    @Override
    public IssueDto create(IssueDto dto) {
        if (repository.existsByTitle(dto.title())) {
            throw new ApiException(HttpStatus.FORBIDDEN, "40302", "Issue title already exists");
        }
        Issue issue = toEntity(new Issue(), dto);
        IssueDto created = toDto(repository.save(issue));
        cache.put(key(created.id()), created);
        cache.evictByPrefix(listPrefix());
        return created;
    }

    @Override
    @Transactional(readOnly = true)
    public IssueDto get(Long id) {
        IssueDto cached = cache.get(key(id), IssueDto.class);
        if (cached != null) {
            return cached;
        }
        IssueDto dto = toDto(findEntity(id));
        cache.put(key(id), dto);
        return dto;
    }

    @Override
    @Transactional(readOnly = true)
    public List<IssueDto> findAll(Pageable pageable) {
        String key = listKey(pageable);
        List<IssueDto> cached = cache.getList(key, IssueDto.class);
        if (cached != null) {
            return cached;
        }
        List<IssueDto> issues = repository.findAll(pageable).stream().map(this::toDto).toList();
        cache.put(key, issues);
        return issues;
    }

    @Override
    public IssueDto update(Long id, IssueDto dto) {
        Issue issue = findEntity(id);
        if (repository.existsByTitleAndIdNot(dto.title(), id)) {
            throw new ApiException(HttpStatus.FORBIDDEN, "40302", "Issue title already exists");
        }
        IssueDto updated = toDto(repository.save(toEntity(issue, dto)));
        cache.put(key(id), updated);
        cache.evictByPrefix(listPrefix());
        return updated;
    }

    @Override
    public void delete(Long id) {
        Issue issue = findEntity(id);
        repository.delete(issue);
        cache.evict(key(id));
        cache.evictByPrefix(listPrefix());
    }

    private Issue findEntity(Long id) {
        if (id == null) {
            throw new ApiException(HttpStatus.BAD_REQUEST, "40005", "Issue id is required");
        }
        return repository.findById(id)
                .orElseThrow(() -> new ApiException(HttpStatus.NOT_FOUND, "40402", "Issue not found"));
    }

    private Issue toEntity(Issue issue, IssueDto dto) {
        Writer writer = writerRepository.findById(dto.writerId())
                .orElseThrow(() -> new ApiException(HttpStatus.BAD_REQUEST, "40002", "Writer association not found"));
        issue.setWriter(writer);
        issue.setTitle(dto.title());
        issue.setContent(dto.content());
        return issue;
    }

    private IssueDto toDto(Issue issue) {
        return new IssueDto(issue.getId(), issue.getWriter().getId(), issue.getTitle(), issue.getContent(),
                issue.getCreated(), issue.getModified());
    }

    private String key(Long id) {
        return "issue:" + id;
    }

    private String listPrefix() {
        return "issue:list:";
    }

    private String listKey(Pageable pageable) {
        return listPrefix() + pageable.getPageNumber() + ":" + pageable.getPageSize();
    }
}
