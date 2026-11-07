package com.distcomp.service.impl;

import com.distcomp.dto.request.NoticeRequestTo;
import com.distcomp.dto.response.NoticeResponseTo;
import com.distcomp.exception.EntityNotFoundException;
import com.distcomp.exception.ValidationException;
import com.distcomp.mapper.NoticeMapper;
import com.distcomp.model.Notice;
import com.distcomp.repository.IssueRepository;
import com.distcomp.repository.NoticeRepository;
import com.distcomp.service.NoticeService;
import com.distcomp.validation.ValidationUtils;
import org.springframework.stereotype.Service;

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
    public NoticeResponseTo create(NoticeRequestTo request) {
        validate(request);
        requireIssue(request.issueId());
        return noticeMapper.toResponse(noticeRepository.save(noticeMapper.toEntity(request)));
    }

    @Override
    public NoticeResponseTo findById(Long id) {
        return noticeMapper.toResponse(findEntity(id));
    }

    @Override
    public List<NoticeResponseTo> findAll() {
        return noticeMapper.toResponseList(noticeRepository.findAll());
    }

    @Override
    public NoticeResponseTo update(Long id, NoticeRequestTo request) {
        ValidationUtils.requireReference("id", id);
        Notice notice = findEntity(id);
        validate(request);
        requireIssue(request.issueId());
        noticeMapper.updateEntity(request, notice);
        return noticeMapper.toResponse(noticeRepository.update(notice));
    }

    @Override
    public void delete(Long id) {
        Notice notice = findEntity(id);
        noticeRepository.deleteById(notice.getId());
    }

    @Override
    public List<NoticeResponseTo> findByIssueId(Long issueId) {
        ValidationUtils.requireReference("issueId", issueId);
        if (!issueRepository.existsById(issueId)) {
            throw new EntityNotFoundException("Issue", issueId);
        }
        return noticeMapper.toResponseList(noticeRepository.findByIssueId(issueId));
    }

    private Notice findEntity(Long id) {
        ValidationUtils.requireReference("id", id);
        return noticeRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException(ENTITY, id));
    }

    private void requireIssue(Long issueId) {
        if (!issueRepository.existsById(issueId)) {
            throw new EntityNotFoundException("Issue", issueId);
        }
    }

    private void validate(NoticeRequestTo request) {
        if (request == null) {
            throw new ValidationException("Request body must be specified");
        }
        ValidationUtils.requireReference("issueId", request.issueId());
        ValidationUtils.requireText("content", request.content(), 2, 2048);
    }
}
