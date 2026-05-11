package com.example.demo.service;

import com.example.demo.entity.Tag;
import com.example.demo.repository.TagRepository;
import com.example.demo.sync.SyncService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.context.annotation.Profile;
import org.springframework.transaction.annotation.Transactional;
import com.example.demo.exception.NotFoundException;
import com.example.demo.dto.TagRequest;
@Profile("docker")
@Service
public class TagService extends AbstractGenericService<Tag, Long> {

    private final TagRepository tagRepository;
    @Autowired
    private SyncService syncService;

    public TagService(TagRepository repository) {
        super(repository, repository);
        this.tagRepository = repository;
    }

    @Override
    public Tag create(Tag entity) {
        Tag saved = super.create(entity);
        syncService.syncTag(saved, "create");
        return saved;
    }

    @Override
    public Tag update(Tag entity) {
        Tag updated = super.update(entity);
        syncService.syncTag(updated, "update");
        return updated;
    }

    @Override
    @Transactional
    public void delete(Long id) {
        if (!tagRepository.existsById(id)) {
            throw new NotFoundException("Tag not found with id: " + id);
        }
        Tag existing = tagRepository.findById(id).get();
        tagRepository.deleteById(id);
        syncService.syncTag(existing, "delete");
    }

    @Transactional
    public Tag createFromRequest(TagRequest request) {
        Tag tag = new Tag();
        tag.setName(request.getName());
        Tag saved = tagRepository.save(tag);
        syncService.syncTag(saved, "create");
        return saved;
    }

    @Transactional
    public Tag updateFromRequest(Long id, TagRequest request) {
        Tag tag = tagRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Tag not found with id: " + id));
        tag.setName(request.getName());
        Tag updated = tagRepository.save(tag);
        syncService.syncTag(updated, "update");
        return updated;
    }
}