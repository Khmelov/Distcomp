package by.bsuir.distcomp.service;

import by.bsuir.distcomp.dto.TagDto;
import by.bsuir.distcomp.exception.ApiException;
import by.bsuir.distcomp.cache.RedisCacheService;
import by.bsuir.distcomp.model.Issue;
import by.bsuir.distcomp.model.Tag;
import by.bsuir.distcomp.repository.IssueRepository;
import by.bsuir.distcomp.repository.TagRepository;
import java.util.List;
import java.util.Optional;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class TagService implements CrudService<TagDto> {
    private final TagRepository repository;
    private final IssueRepository issueRepository;
    private final RedisCacheService cache;

    public TagService(TagRepository repository, IssueRepository issueRepository, RedisCacheService cache) {
        this.repository = repository;
        this.issueRepository = issueRepository;
        this.cache = cache;
    }

    @Override
    public TagDto create(TagDto dto) {
        if (repository.existsByName(dto.name())) {
            throw new ApiException(HttpStatus.FORBIDDEN, "40304", "Tag name already exists");
        }
        Tag tag = repository.save(toEntity(new Tag(), dto));
        attachIssueIfPresent(tag, dto.issueId());
        TagDto created = toDto(tag);
        cache.put(key(created.id()), created);
        cache.evictByPrefix(listPrefix());
        return created;
    }

    @Override
    @Transactional(readOnly = true)
    public TagDto get(Long id) {
        TagDto cached = cache.get(key(id), TagDto.class);
        if (cached != null) {
            return cached;
        }
        TagDto dto = toDto(findEntity(id));
        cache.put(key(id), dto);
        return dto;
    }

    @Override
    @Transactional(readOnly = true)
    public List<TagDto> findAll(Pageable pageable) {
        String key = listKey(pageable);
        List<TagDto> cached = cache.getList(key, TagDto.class);
        if (cached != null) {
            return cached;
        }
        List<TagDto> tags = repository.findAll(pageable).stream().map(this::toDto).toList();
        cache.put(key, tags);
        return tags;
    }

    @Override
    public TagDto update(Long id, TagDto dto) {
        Tag tag = findEntity(id);
        if (repository.existsByNameAndIdNot(dto.name(), id)) {
            throw new ApiException(HttpStatus.FORBIDDEN, "40304", "Tag name already exists");
        }
        tag = repository.save(toEntity(tag, dto));
        if (dto.issueId() != null) {
            detachFromIssues(tag);
            attachIssueIfPresent(tag, dto.issueId());
        }
        TagDto updated = toDto(tag);
        cache.put(key(id), updated);
        cache.evictByPrefix(listPrefix());
        return updated;
    }

    @Override
    public void delete(Long id) {
        Tag tag = findEntity(id);
        repository.delete(tag);
        cache.evict(key(id));
        cache.evictByPrefix(listPrefix());
    }

    private Tag findEntity(Long id) {
        if (id == null) {
            throw new ApiException(HttpStatus.BAD_REQUEST, "40007", "Tag id is required");
        }
        return repository.findById(id)
                .orElseThrow(() -> new ApiException(HttpStatus.NOT_FOUND, "40404", "Tag not found"));
    }

    private Tag toEntity(Tag tag, TagDto dto) {
        tag.setName(dto.name());
        return tag;
    }

    private TagDto toDto(Tag tag) {
        Long issueId = tag.getIssues().stream().findFirst().map(Issue::getId).orElse(null);
        return new TagDto(tag.getId(), issueId, tag.getName());
    }

    private void attachIssueIfPresent(Tag tag, Long issueId) {
        if (issueId == null) {
            return;
        }
        Issue issue = issueRepository.findById(issueId)
                .orElseThrow(() -> new ApiException(HttpStatus.BAD_REQUEST, "40008", "Issue association not found"));
        issue.getTags().add(tag);
        tag.getIssues().add(issue);
        issueRepository.save(issue);
    }

    private void detachFromIssues(Tag tag) {
        for (Issue issue : List.copyOf(tag.getIssues())) {
            issue.getTags().remove(tag);
            tag.getIssues().remove(issue);
        }
    }

    private String key(Long id) {
        return "tag:" + id;
    }

    private String listPrefix() {
        return "tag:list:";
    }

    private String listKey(Pageable pageable) {
        return listPrefix() + pageable.getPageNumber() + ":" + pageable.getPageSize();
    }
}
