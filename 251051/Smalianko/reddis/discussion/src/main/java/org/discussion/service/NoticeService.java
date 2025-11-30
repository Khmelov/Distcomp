package org.discussion.service;

import com.common.NoticeAsyncResponse;
import com.common.NoticeMessage;
import com.common.NoticeResponseToDto;
import com.common.NoticeState;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.discussion.dto.request.NoticeRequestToDto;
import org.discussion.exception.NotFoundException;
import org.discussion.model.Notice;
import org.discussion.model.NoticeKey;
import org.discussion.repository.NoticeRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;
import java.util.stream.Stream;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class NoticeService {

    private static final String COUNTRY = "US";

    CompositeIdCodec compositeIdCodec;
    NoticeRepository repository;

    public NoticeMessage create(NoticeMessage message) {
        if (moderate(message.getContent())) {
            message.setState(NoticeState.DECLINE);
            return message;
        }

        var key = compositeIdCodec.decode(message.getId());
        var notice = new Notice(key, message.getContent());

        repository.save(notice);

        message.setState(NoticeState.APPROVE);
        return message;
    }

    public NoticeResponseToDto create(NoticeRequestToDto req) {
        validate(req.content());
        long newId = compositeIdCodec.encode(COUNTRY, req.issueId(), generateId());
        NoticeKey key = compositeIdCodec.decode(newId);
        Notice notice = new Notice(key, req.content());
        repository.save(notice);
        return toDto(notice);
    }

    public NoticeAsyncResponse getAll(String correlationId) {
        var list = repository.findAll().stream()
                .toList();

        return toAsyncResponse(list, correlationId);
    }

    public List<NoticeResponseToDto> getAll() {
        return repository.findAll().stream()
                .map(this::toDto)
                .toList();
    }


    public NoticeAsyncResponse getById(Long compositeId, String correlationId) {
        NoticeKey key = compositeIdCodec.decode(compositeId);

        Notice notice = repository.findById(key)
                .orElseThrow(() -> new NotFoundException("Notice not found"));

        return toAsyncResponse(List.of(notice), correlationId);
    }

    public NoticeResponseToDto getById(Long compositeId) {
        NoticeKey key = compositeIdCodec.decode(compositeId);

        Notice notice = repository.findById(key)
                .orElseThrow(() -> new NotFoundException("Notice not found"));

        return toDto(notice);
    }

    public List<NoticeResponseToDto> getByIssueId(Long issueId) {
        return repository.findAll().stream()
                .filter(n -> n.getKey().getIssueId().equals(issueId))
                .map(this::toDto)
                .toList();
    }

    public NoticeAsyncResponse update(NoticeMessage message) {
        validate(message.getContent());
        var key = compositeIdCodec.decode(message.getId());
        var notice = repository.findById(key)
                .orElseThrow(() -> new NotFoundException("Notice not found"));
        notice.setContent(message.getContent());
        repository.save(notice);
        return toAsyncResponse(List.of(notice), message.getCorrelationId());
    }

    public NoticeResponseToDto update(Long compositeId, NoticeRequestToDto req) {
        validate(req.content());

        NoticeKey key = compositeIdCodec.decode(compositeId);

        Notice notice = repository.findById(key)
                .orElseThrow(() -> new NotFoundException("Notice not found"));

        notice.setContent(req.content());

        repository.save(notice);

        return toDto(notice);
    }

    public void delete(Long compositeId) {
        NoticeKey key = compositeIdCodec.decode(compositeId);
        Notice notice = repository.findById(key)
                .orElseThrow(() -> new NotFoundException("Notice not found"));
        repository.delete(notice);
    }

    private boolean moderate(String content) {
        return Stream.of("bad", "spam", "hate", "offensive")
                .anyMatch(content::contains);
    }

    private void validate(String content) {
        if (content == null || content.length() < 2 || content.length() > 2048) {
            throw new RuntimeException("content must be 2..2048 chars");
        }
    }

    private NoticeAsyncResponse toAsyncResponse(List<Notice> notices, String correlationId) {
        return new NoticeAsyncResponse(
                notices.stream()
                        .map(this::toDto)
                        .toList(),
                correlationId
        );
    }

    private NoticeResponseToDto toDto(Notice notice) {
        var key = compositeIdCodec.encode(
                notice.getKey().getCountry(),
                notice.getKey().getIssueId(),
                notice.getKey().getId()
        );
        return new NoticeResponseToDto(
                key,
                notice.getKey().getIssueId(),
                notice.getContent(),
                NoticeState.APPROVE
        );
    }

    private long generateId() {
        return Math.abs(UUID.randomUUID().getMostSignificantBits());
    }
}