package com.distcomp.service.impl;

import com.distcomp.dto.request.CreatorRequestTo;
import com.distcomp.dto.response.CreatorResponseTo;
import com.distcomp.exception.DuplicateEntityException;
import com.distcomp.exception.EntityNotFoundException;
import com.distcomp.exception.ValidationException;
import com.distcomp.mapper.CreatorMapper;
import com.distcomp.model.Creator;
import com.distcomp.model.Issue;
import com.distcomp.repository.CreatorRepository;
import com.distcomp.repository.IssueRepository;
import com.distcomp.repository.NoticeRepository;
import com.distcomp.service.CreatorService;
import com.distcomp.validation.ValidationUtils;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * Default {@link CreatorService} implementation.
 *
 * <p>Owns validation, DTO/entity mapping and the cascading removal of the
 * issues (and their notices) that belong to a deleted creator.</p>
 */
@Service
public class CreatorServiceImpl implements CreatorService {

    private static final String ENTITY = "Creator";

    private final CreatorRepository creatorRepository;

    private final IssueRepository issueRepository;

    private final NoticeRepository noticeRepository;

    private final CreatorMapper creatorMapper;

    public CreatorServiceImpl(CreatorRepository creatorRepository,
                              IssueRepository issueRepository,
                              NoticeRepository noticeRepository,
                              CreatorMapper creatorMapper) {
        this.creatorRepository = creatorRepository;
        this.issueRepository = issueRepository;
        this.noticeRepository = noticeRepository;
        this.creatorMapper = creatorMapper;
    }

    @Override
    public CreatorResponseTo create(CreatorRequestTo request) {
        validate(request);
        creatorRepository.findByLogin(request.login()).ifPresent(existing -> {
            throw new DuplicateEntityException(ENTITY, "login", request.login());
        });
        Creator creator = creatorMapper.toEntity(request);
        return creatorMapper.toResponse(creatorRepository.save(creator));
    }

    @Override
    public CreatorResponseTo findById(Long id) {
        return creatorMapper.toResponse(findEntity(id));
    }

    @Override
    public List<CreatorResponseTo> findAll() {
        return creatorMapper.toResponseList(creatorRepository.findAll());
    }

    @Override
    public CreatorResponseTo update(Long id, CreatorRequestTo request) {
        ValidationUtils.requireReference("id", id);
        Creator creator = findEntity(id);
        validate(request);
        creatorMapper.updateEntity(request, creator);
        return creatorMapper.toResponse(creatorRepository.update(creator));
    }

    @Override
    public void delete(Long id) {
        Creator creator = findEntity(id);
        for (Issue issue : issueRepository.findByCreatorId(id)) {
            noticeRepository.findByIssueId(issue.getId())
                    .forEach(notice -> noticeRepository.deleteById(notice.getId()));
            issueRepository.deleteById(issue.getId());
        }
        creatorRepository.deleteById(creator.getId());
    }

    @Override
    public CreatorResponseTo findByIssueId(Long issueId) {
        Issue issue = issueRepository.findById(issueId)
                .orElseThrow(() -> new EntityNotFoundException("Issue", issueId));
        return findById(issue.getCreatorId());
    }

    private Creator findEntity(Long id) {
        ValidationUtils.requireReference("id", id);
        return creatorRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException(ENTITY, id));
    }

    private void validate(CreatorRequestTo request) {
        if (request == null) {
            throw new ValidationException("Request body must be specified");
        }
        ValidationUtils.requireText("login", request.login(), 2, 64);
        ValidationUtils.requireText("password", request.password(), 8, 128);
        ValidationUtils.requireText("firstname", request.firstname(), 2, 64);
        ValidationUtils.requireText("lastname", request.lastname(), 2, 64);
    }
}
