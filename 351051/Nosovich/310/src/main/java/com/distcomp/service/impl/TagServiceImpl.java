package com.distcomp.service.impl;

import com.distcomp.dto.request.TagRequestTo;
import com.distcomp.dto.response.TagResponseTo;
import com.distcomp.exception.EntityNotFoundException;
import com.distcomp.exception.ValidationException;
import com.distcomp.mapper.TagMapper;
import com.distcomp.model.Issue;
import com.distcomp.model.Tag;
import com.distcomp.repository.IssueRepository;
import com.distcomp.repository.TagRepository;
import com.distcomp.service.TagService;
import com.distcomp.validation.ValidationUtils;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

/**
 * Default {@link TagService} implementation.
 */
@Service
public class TagServiceImpl implements TagService {

    private static final String ENTITY = "Tag";

    private final TagRepository tagRepository;

    private final IssueRepository issueRepository;

    private final TagMapper tagMapper;

    public TagServiceImpl(TagRepository tagRepository,
                          IssueRepository issueRepository,
                          TagMapper tagMapper) {
        this.tagRepository = tagRepository;
        this.issueRepository = issueRepository;
        this.tagMapper = tagMapper;
    }

    @Override
    public TagResponseTo create(TagRequestTo request) {
        validate(request);
        return tagMapper.toResponse(tagRepository.save(tagMapper.toEntity(request)));
    }

    @Override
    public TagResponseTo findById(Long id) {
        return tagMapper.toResponse(findEntity(id));
    }

    @Override
    public List<TagResponseTo> findAll() {
        return tagMapper.toResponseList(tagRepository.findAll());
    }

    @Override
    public TagResponseTo update(Long id, TagRequestTo request) {
        ValidationUtils.requireReference("id", id);
        Tag tag = findEntity(id);
        validate(request);
        tagMapper.updateEntity(request, tag);
        return tagMapper.toResponse(tagRepository.update(tag));
    }

    @Override
    public void delete(Long id) {
        Tag tag = findEntity(id);
        for (Issue issue : issueRepository.findByTagId(tag.getId())) {
            List<Long> remaining = new ArrayList<>(issue.getTagIds());
            remaining.remove(tag.getId());
            issue.setTagIds(remaining);
            issueRepository.update(issue);
        }
        tagRepository.deleteById(tag.getId());
    }

    @Override
    public List<TagResponseTo> findByIssueId(Long issueId) {
        ValidationUtils.requireReference("issueId", issueId);
        Issue issue = issueRepository.findById(issueId)
                .orElseThrow(() -> new EntityNotFoundException("Issue", issueId));
        List<Tag> tags = issue.getTagIds() == null
                ? List.of()
                : issue.getTagIds().stream()
                        .map(tagRepository::findById)
                        .flatMap(java.util.Optional::stream)
                        .toList();
        return tagMapper.toResponseList(tags);
    }

    private Tag findEntity(Long id) {
        ValidationUtils.requireReference("id", id);
        return tagRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException(ENTITY, id));
    }

    private void validate(TagRequestTo request) {
        if (request == null) {
            throw new ValidationException("Request body must be specified");
        }
        ValidationUtils.requireText("name", request.name(), 2, 32);
    }
}
