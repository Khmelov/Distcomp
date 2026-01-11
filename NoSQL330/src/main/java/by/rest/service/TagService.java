package by.rest.service;

import by.rest.domain.Tag;
import by.rest.dto.TagRequestTo;
import by.rest.dto.TagResponseTo;
import by.rest.exception.ApiException;
import by.rest.mapper.TagMapper;
import by.rest.repository.TagRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional
public class TagService {
    private final TagRepository repo;
    private final TagMapper mapper;

    public TagService(TagRepository repo, TagMapper mapper) {
        this.repo = repo;
        this.mapper = mapper;
    }

    public TagResponseTo create(TagRequestTo req) {
        validate(req);
        Tag tag = mapper.toEntity(req);
        tag = repo.save(tag);
        return mapper.toResponse(tag);
    }

    @Transactional(readOnly = true)
    public List<TagResponseTo> getAll() {
        return repo.findAll().stream()
                .map(mapper::toResponse)
                .toList();
    }

    @Transactional(readOnly = true)
    public TagResponseTo getById(Long id) {
        Tag tag = repo.findById(id)
                .orElseThrow(() -> new ApiException(404, "40401", "Tag not found"));
        return mapper.toResponse(tag);
    }

    public TagResponseTo update(Long id, TagRequestTo req) {
        validate(req);
        Tag tag = repo.findById(id)
                .orElseThrow(() -> new ApiException(404, "40401", "Tag not found"));
        tag.setName(req.getName());
        tag = repo.save(tag);
        return mapper.toResponse(tag);
    }

    public void delete(Long id) {
        if (!repo.existsById(id)) {
            throw new ApiException(404, "40401", "Tag not found");
        }
        repo.deleteById(id);
    }

    private void validate(TagRequestTo req) {
        if (req.getName() == null || req.getName().length() < 2 || req.getName().length() > 32) {
            throw new ApiException(400, "40002", "Tag name must be between 2 and 32 characters");
        }
    }
}