package com.distcomp.service.impl;

import com.distcomp.dto.filter.NoticeFilter;
import com.distcomp.dto.request.NoticeRequestTo;
import com.distcomp.dto.response.NoticeResponseTo;
import com.distcomp.exception.EntityNotFoundException;
import com.distcomp.exception.ValidationException;
import com.distcomp.mapper.NoticeMapper;
import com.distcomp.model.Issue;
import com.distcomp.model.Notice;
import com.distcomp.pagination.PagedResult;
import com.distcomp.repository.IssueRepository;
import com.distcomp.repository.NoticeRepository;
import com.distcomp.service.NoticeService;
import com.distcomp.validation.ValidationUtils;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

/**
 * Default {@link NoticeService} implementation.
 */
@Service
public class NoticeServiceImpl implements NoticeService {

    private static final String ENTITY = "Notice";

    private final NoticeRepository noticeRepository;

    private final IssueRepository issueRepository;

    private final NoticeMapper noticeMapper;

    public NoticeServiceImpl(NoticeRepository noticeRepository,
                             IssueRepository issueRepository,
                             NoticeMapper noticeMapper) {
        this.noticeRepository = noticeRepository;
        this.issueRepository = issueRepository;
        this.noticeMapper = noticeMapper;
    }

    @Override
    @Transactional
    public NoticeResponseTo create(NoticeRequestTo request) {
        validate(request);
        Notice notice = noticeMapper.toEntity(request);
        notice.setIssue(requireIssue(request.issueId()));
        return noticeMapper.toResponse(noticeRepository.save(notice));
    }

    @Override
    @Transactional(readOnly = true)
    public NoticeResponseTo findById(Long id) {
        return noticeMapper.toResponse(findEntity(id));
    }

    @Override
    @Transactional(readOnly = true)
    public PagedResult<NoticeResponseTo> findAll(NoticeFilter filter, Pageable pageable) {
        Page<Notice> page = noticeRepository.findAll(toSpecification(filter), pageable);
        return new PagedResult<>(noticeMapper.toResponseList(page.getContent()), page.getTotalElements());
    }

    @Override
    @Transactional
    public NoticeResponseTo update(Long id, NoticeRequestTo request) {
        ValidationUtils.requireReference("id", id);
        Notice notice = findEntity(id);
        validate(request);
        noticeMapper.updateEntity(request, notice);
        notice.setIssue(requireIssue(request.issueId()));
        return noticeMapper.toResponse(noticeRepository.save(notice));
    }

    @Override
    @Transactional
    public void delete(Long id) {
        noticeRepository.delete(findEntity(id));
    }

    @Override
    @Transactional(readOnly = true)
    public List<NoticeResponseTo> findByIssueId(Long issueId) {
        ValidationUtils.requireReference("issueId", issueId);
        requireIssue(issueId);
        return noticeMapper.toResponseList(noticeRepository.findByIssueId(issueId));
    }

    private Notice findEntity(Long id) {
        ValidationUtils.requireReference("id", id);
        return noticeRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException(ENTITY, id));
    }

    private Issue requireIssue(Long issueId) {
        ValidationUtils.requireReference("issueId", issueId);
        return issueRepository.findById(issueId)
                .orElseThrow(() -> new EntityNotFoundException("Issue", issueId));
    }

    private Specification<Notice> toSpecification(NoticeFilter filter) {
        return (root, query, builder) -> {
            List<jakarta.persistence.criteria.Predicate> predicates = new ArrayList<>();
            if (filter != null && filter.issueId() != null) {
                predicates.add(builder.equal(root.get("issue").get("id"), filter.issueId()));
            }
            return builder.and(predicates.toArray(new jakarta.persistence.criteria.Predicate[0]));
        };
    }

    private void validate(NoticeRequestTo request) {
        if (request == null) {
            throw new ValidationException("Request body must be specified");
        }
        ValidationUtils.requireReference("issueId", request.issueId());
        ValidationUtils.requireText("content", request.content(), 2, 2048);
    }
}
