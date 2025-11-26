package org.discussion.service;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.discussion.dto.request.NoticeRequestToDto;
import org.discussion.dto.response.NoticeResponseToDto;
import org.discussion.model.Notice;
import org.discussion.model.NoticeKey;
import org.discussion.repository.NoticeRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class NoticeService {

    private static final String COUNTRY = "US";

    CompositeIdCodec compositeIdCodec;
    NoticeRepository repository;

    public NoticeResponseToDto create(NoticeRequestToDto req) {
        validate(req);

        long newId = compositeIdCodec.encode(COUNTRY, req.issueId(), generateId());

        NoticeKey key = compositeIdCodec.decode(newId);

        Notice notice = new Notice(key, req.content());

        repository.save(notice);

        return toDto(notice);
    }

    public List<NoticeResponseToDto> getAll() {
        return repository.findAll().stream()
                .map(this::toDto)
                .toList();
    }

    public NoticeResponseToDto getById(Long compositeId) {
        NoticeKey key = compositeIdCodec.decode(compositeId);

        Notice notice = repository.findById(key)
                .orElseThrow(() -> new RuntimeException("Notice not found"));

        return toDto(notice);
    }

    public List<NoticeResponseToDto> getByIssueId(Long issueId) {
        return repository.findAll().stream()
                .filter(n -> n.getKey().getIssueId().equals(issueId))
                .map(this::toDto)
                .toList();
    }

    public NoticeResponseToDto update(Long compositeId, NoticeRequestToDto req) {
        validate(req);

        NoticeKey key = compositeIdCodec.decode(compositeId);

        Notice notice = repository.findById(key)
                .orElseThrow(() -> new RuntimeException("Notice not found"));

        notice.setContent(req.content());

        repository.save(notice);

        return toDto(notice);
    }

    public void delete(Long compositeId) {
        NoticeKey key = compositeIdCodec.decode(compositeId);
        repository.deleteById(key);
    }

    private void validate(NoticeRequestToDto req) {
        if (req.content() == null || req.content().length() < 2 || req.content().length() > 2048) {
            throw new RuntimeException("content must be 2..2048 chars");
        }
    }

    private NoticeResponseToDto toDto(Notice notice) {
        var key = compositeIdCodec.encode(
                notice.getKey().getCountry(),
                notice.getKey().getIssueId(),
                notice.getKey().getId()
        );
        return new NoticeResponseToDto(
                notice.getKey().getCountry(),
                notice.getKey().getIssueId(),
                key,
                notice.getContent()
        );
    }

    private long generateId() {
        return Math.abs(UUID.randomUUID().getMostSignificantBits());
    }
}