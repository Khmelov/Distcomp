package org.example;

import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
@Transactional
public class NoticeService {

    private final NoticeRepository noticeRepository;
    private final StoryRepository storyRepository;
    private final NoticeMapper noticeMapper;

    public NoticeService(
            NoticeRepository noticeRepository,
            StoryRepository storyRepository,
            NoticeMapper noticeMapper
    ) {
        this.noticeRepository = noticeRepository;
        this.storyRepository = storyRepository;
        this.noticeMapper = noticeMapper;
    }

    // ---------- CREATE ----------
    public NoticeResponseTo create(NoticeRequestTo dto) {

        Notice notice = noticeMapper.toEntity(dto);

        Story story = storyRepository.findById(dto.getStoryId())
                .orElseThrow(() -> new EntityNotFoundException("Story not found"));

        notice.setStory(story);

        Notice saved = noticeRepository.save(notice);
        return noticeMapper.toResponse(saved);
    }

    // ---------- READ ----------
    public NoticeResponseTo getById(Long id) {
        Notice notice = noticeRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Notice not found"));
        return noticeMapper.toResponse(notice);
    }

    public List<NoticeResponseTo> getAll() {
        return noticeRepository.findAll()
                .stream()
                .map(noticeMapper::toResponse)
                .toList();
    }

    // ---------- UPDATE ----------
    public NoticeResponseTo update(Long id, NoticeRequestTo dto) {
        Notice notice = noticeRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Notice not found"));

        notice.setContent(dto.getContent());
        return noticeMapper.toResponse(notice);
    }

    // ---------- DELETE ----------
    public void delete(Long id) {
        if (!noticeRepository.existsById(id)) {
            throw new EntityNotFoundException("Notice not found");
        }
        noticeRepository.deleteById(id);
    }
}