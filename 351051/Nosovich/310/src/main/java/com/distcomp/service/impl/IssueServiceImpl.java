package com.distcomp.service.impl;

import com.distcomp.dto.request.IssueRequestTo;
import com.distcomp.dto.response.IssueResponseTo;
import com.distcomp.exception.EntityNotFoundException;
import com.distcomp.exception.ValidationException;
import com.distcomp.mapper.IssueMapper;
import com.distcomp.model.Issue;
import com.distcomp.repository.CreatorRepository;
import com.distcomp.repository.IssueRepository;
import com.distcomp.repository.NoticeRepository;
import com.distcomp.repository.TagRepository;
import com.distcomp.service.IssueService;
import com.distcomp.validation.ValidationUtils;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

/**
 * Default {@link IssueService} implementation.
 *
 * <p>Guarantees that an issue always references an existing creator and only
 * existing tags, and that the {@code created} / {@code modified} stamps are
 * produced by the application, never by a client.</p>
 */
@Service
public class IssueServiceImpl implements IssueService {

    private static final String ENTITY = "Issue";

    private final IssueRepository issueRepository;

    private final CreatorRepository creatorRepository;

    private final TagRepository tagRepository;

    private final NoticeRepository noticeRepository;

    private final IssueMapper issueMapper;

    public IssueServiceImpl(IssueRepository issueRepository,
                            CreatorRepository creatorRepository,
                            TagRepository tagRepository,
                            NoticeRepository noticeRepository,
                            IssueMapper issueMapper) {
        this.issueRepository = issueRepository;
        this.creatorRepository = creatorRepository;
        this.tagRepository = tagRepository;
        this.noticeRepository = noticeRepository;
        this.issueMapper = issueMapper;
    }

    @Override
    public IssueResponseTo create(IssueRequestTo request) {
        validate(request);
        requireCreator(request.creatorId());
        requireTags(request.tagIds());

        Issue issue = issueMapper.toEntity(request);
        issue.setTagIds(request.tagIds() == null ? new ArrayList<>() : new ArrayList<>(request.tagIds()));
        Instant now = Instant.now();
        issue.setCreated(now);
        issue.setModified(now);
        return issueMapper.toResponse(issueRepository.save(issue));
    }

    @Override
    public IssueResponseTo findById(Long id) {
        return issueMapper.toResponse(findEntity(id));
    }

    @Override
    public List<IssueResponseTo> findAll() {
        return issueMapper.toResponseList(issueRepository.findAll());
    }

    @Override
    public IssueResponseTo update(Long id, IssueRequestTo request) {
        ValidationUtils.requireReference("id", id);
        Issue issue = findEntity(id);
        validate(request);
        requireCreator(request.creatorId());
        requireTags(request.tagIds());

        issueMapper.updateEntity(request, issue);
        if (request.tagIds() != null) {
            issue.setTagIds(new ArrayList<>(request.tagIds()));
        }
        issue.setModified(Instant.now());
        return issueMapper.toResponse(issueRepository.update(issue));
    }

    @Override
    public void delete(Long id) {
        Issue issue = findEntity(id);
        noticeRepository.findByIssueId(issue.getId())
                .forEach(notice -> noticeRepository.deleteById(notice.getId()));
        issueRepository.deleteById(issue.getId());
    }

    @Override
    public List<IssueResponseTo> findByCreatorId(Long creatorId) {
        ValidationUtils.requireReference("creatorId", creatorId);
        return issueMapper.toResponseList(issueRepository.findByCreatorId(creatorId));
    }

    @Override
    public List<IssueResponseTo> findByTagId(Long tagId) {
        ValidationUtils.requireReference("tagId", tagId);
        return issueMapper.toResponseList(issueRepository.findByTagId(tagId));
    }

    private Issue findEntity(Long id) {
        ValidationUtils.requireReference("id", id);
        return issueRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException(ENTITY, id));
    }

    private void requireCreator(Long creatorId) {
        if (!creatorRepository.existsById(creatorId)) {
            throw new EntityNotFoundException("Creator", creatorId);
        }
    }

    private void requireTags(List<Long> tagIds) {
        if (tagIds == null) {
            return;
        }
        for (Long tagId : tagIds) {
            if (!tagRepository.existsById(tagId)) {
                throw new EntityNotFoundException("Tag", tagId);
            }
        }
    }

    private void validate(IssueRequestTo request) {
        if (request == null) {
            throw new ValidationException("Request body must be specified");
        }
        ValidationUtils.requireReference("creatorId", request.creatorId());
        ValidationUtils.requireText("title", request.title(), 2, 64);
        ValidationUtils.requireText("content", request.content(), 4, 2048);
    }
}
