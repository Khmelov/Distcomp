package com.distcomp.discussion.service.impl;

import com.distcomp.common.notice.NoticeDto;
import com.distcomp.common.notice.NoticeState;
import com.distcomp.discussion.dto.NoticePage;
import com.distcomp.discussion.dto.NoticeRequestTo;
import com.distcomp.discussion.exception.EntityNotFoundException;
import com.distcomp.discussion.exception.ValidationException;
import com.distcomp.discussion.model.Notice;
import com.distcomp.discussion.model.NoticeById;
import com.distcomp.discussion.model.NoticeKey;
import com.distcomp.discussion.repository.NoticeByIdRepository;
import com.distcomp.discussion.repository.NoticeRepository;
import com.distcomp.discussion.service.ModerationService;
import com.distcomp.discussion.service.NoticeIdGenerator;
import com.distcomp.discussion.service.NoticeService;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Locale;

/**
 * Default {@link NoticeService} implementation.
 *
 * <p>Each notice is written twice: into the issue-partitioned table for range
 * queries and into the identifier-keyed table for point lookups. Cassandra does
 * not provide multi-table transactions, so the two writes are performed in a
 * deterministic order and the application treats the lookup row as the source
 * of truth for point operations.</p>
 */
@Service
public class NoticeServiceImpl implements NoticeService {

    private static final int DEFAULT_PAGE_SIZE = 20;

    private final NoticeRepository noticeRepository;

    private final NoticeByIdRepository noticeByIdRepository;

    private final NoticeIdGenerator idGenerator;

    private final ModerationService moderationService;

    public NoticeServiceImpl(NoticeRepository noticeRepository,
                             NoticeByIdRepository noticeByIdRepository,
                             NoticeIdGenerator idGenerator,
                             ModerationService moderationService) {
        this.noticeRepository = noticeRepository;
        this.noticeByIdRepository = noticeByIdRepository;
        this.idGenerator = idGenerator;
        this.moderationService = moderationService;
    }

    @Override
    public NoticeDto create(NoticeRequestTo request) {
        validate(request);
        long id = idGenerator.nextId();
        NoticeState state = moderationService.moderate(request.content());
        save(id, request.issueId(), request.content(), state);
        return new NoticeDto(id, request.issueId(), request.content(), state);
    }

    @Override
    public NoticeDto findById(Long id) {
        return toDto(findLookup(id));
    }

    @Override
    public NoticePage findAll(Long issueId, Integer page, Integer size, String sort) {
        List<NoticeDto> notices = load(issueId);
        notices.sort(comparator(sort));

        if (page == null && size == null) {
            return new NoticePage(notices, notices.size());
        }

        int pageNumber = page == null ? 0 : Math.max(page, 0);
        int pageSize = size == null ? DEFAULT_PAGE_SIZE : Math.max(size, 1);
        int from = Math.min(pageNumber * pageSize, notices.size());
        int to = Math.min(from + pageSize, notices.size());
        return new NoticePage(notices.subList(from, to), notices.size());
    }

    @Override
    public NoticeDto update(Long id, NoticeRequestTo request) {
        validate(request);
        NoticeById existing = findLookup(id);

        if (!existing.getIssueId().equals(request.issueId())) {
            noticeRepository.deleteById(new NoticeKey(existing.getIssueId(), id));
        }

        NoticeState state = moderationService.moderate(request.content());
        save(id, request.issueId(), request.content(), state);
        return new NoticeDto(id, request.issueId(), request.content(), state);
    }

    @Override
    public void delete(Long id) {
        NoticeById existing = findLookup(id);
        noticeByIdRepository.deleteById(id);
        noticeRepository.deleteById(new NoticeKey(existing.getIssueId(), id));
    }

    @Override
    public long deleteByIssueId(Long issueId) {
        if (issueId == null) {
            throw new ValidationException("issueId", "must be specified");
        }
        List<Notice> notices = noticeRepository.findByKeyIssueId(issueId);
        for (Notice notice : notices) {
            noticeByIdRepository.deleteById(notice.getKey().getId());
            noticeRepository.delete(notice);
        }
        return notices.size();
    }

    private List<NoticeDto> load(Long issueId) {
        List<NoticeDto> result = new ArrayList<>();
        if (issueId != null) {
            for (Notice notice : noticeRepository.findByKeyIssueId(issueId)) {
                result.add(toDto(notice));
            }
            return result;
        }
        for (NoticeById notice : noticeByIdRepository.findAll()) {
            result.add(toDto(notice));
        }
        return result;
    }

    private void save(long id, Long issueId, String content, NoticeState state) {
        Notice notice = new Notice(new NoticeKey(issueId, id), content, state.name());
        NoticeById lookup = new NoticeById(id, issueId, content, state.name());
        noticeRepository.save(notice);
        noticeByIdRepository.save(lookup);
    }

    private NoticeById findLookup(Long id) {
        if (id == null) {
            throw new ValidationException("id", "must be specified");
        }
        return noticeByIdRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException(id));
    }

    private NoticeDto toDto(NoticeById notice) {
        return new NoticeDto(notice.getId(), notice.getIssueId(), notice.getContent(), stateOf(notice.getState()));
    }

    private NoticeDto toDto(Notice notice) {
        return new NoticeDto(notice.getKey().getId(), notice.getKey().getIssueId(),
                notice.getContent(), stateOf(notice.getState()));
    }

    private NoticeState stateOf(String value) {
        NoticeState state = NoticeState.fromValue(value);
        return state == null ? NoticeState.PENDING : state;
    }

    private Comparator<NoticeDto> comparator(String sort) {
        String expression = sort == null || sort.isBlank() ? "id,asc" : sort;
        String[] tokens = expression.split(",");
        String property = tokens[0].trim().toLowerCase(Locale.ROOT);
        boolean descending = tokens.length > 1 && "desc".equalsIgnoreCase(tokens[1].trim());

        Comparator<NoticeDto> comparator = switch (property) {
            case "content" -> Comparator.comparing(NoticeDto::content,
                    Comparator.nullsLast(String.CASE_INSENSITIVE_ORDER));
            case "state" -> Comparator.comparing(NoticeDto::state,
                    Comparator.nullsLast(Comparator.naturalOrder()));
            case "issueid", "issue_id" -> Comparator.comparing(NoticeDto::issueId,
                    Comparator.nullsLast(Comparator.naturalOrder()));
            case "id" -> Comparator.comparing(NoticeDto::id, Comparator.nullsLast(Comparator.naturalOrder()));
            default -> throw new ValidationException("sort",
                    "contains unsupported property '" + property + "'");
        };
        return descending ? comparator.reversed() : comparator;
    }

    private void validate(NoticeRequestTo request) {
        if (request == null) {
            throw new ValidationException("Request body must be specified");
        }
        if (request.issueId() == null) {
            throw new ValidationException("issueId", "must be specified");
        }
        if (request.content() == null || request.content().isBlank()) {
            throw new ValidationException("content", "must be specified");
        }
        if (request.content().length() < 2 || request.content().length() > 2048) {
            throw new ValidationException("content", "length must be between 2 and 2048 characters");
        }
    }
}
