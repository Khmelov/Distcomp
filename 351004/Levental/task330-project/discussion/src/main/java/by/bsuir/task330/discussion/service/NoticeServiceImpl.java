package by.bsuir.task330.discussion.service;

import by.bsuir.task330.discussion.dto.NoticeRequestTo;
import by.bsuir.task330.discussion.dto.NoticeResponseTo;
import by.bsuir.task330.discussion.entity.NoticeEntity;
import by.bsuir.task330.discussion.entity.NoticeKey;
import by.bsuir.task330.discussion.error.NotFoundException;
import by.bsuir.task330.discussion.error.ValidationException;
import by.bsuir.task330.discussion.repository.NoticeRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Comparator;
import java.util.List;

@Service
@Transactional
public class NoticeServiceImpl implements NoticeService {

    private final NoticeRepository repository;

    public NoticeServiceImpl(NoticeRepository repository) {
        this.repository = repository;
    }

    @Override
    public NoticeResponseTo create(NoticeRequestTo request) {
        long nextId = nextId(request.articleId());
        NoticeEntity entity = new NoticeEntity(new NoticeKey(request.articleId(), nextId), request.content());
        NoticeEntity saved = repository.save(entity);
        return toResponse(saved);
    }

    @Override
    public NoticeResponseTo update(NoticeRequestTo request) {
        if (request.id() == null) {
            throw new ValidationException("id is required");
        }
        NoticeKey key = new NoticeKey(request.articleId(), request.id());
        NoticeEntity entity = repository.findById(key)
                .orElseThrow(() -> new NotFoundException("Notice not found"));
        entity.setContent(request.content());
        return toResponse(repository.save(entity));
    }

    @Override
    @Transactional(readOnly = true)
    public NoticeResponseTo findById(Long id) {
        return repository.findAll().stream()
                .filter(entity -> entity.getKey().getId().equals(id))
                .findFirst()
                .map(this::toResponse)
                .orElseThrow(() -> new NotFoundException("Notice not found"));
    }

    @Override
    @Transactional(readOnly = true)
    public List<NoticeResponseTo> findAll(Integer page, Integer size, String sort, String filter, Long articleId) {
        int pageNumber = page == null ? 0 : page;
        int pageSize = size == null ? 10 : size;

        List<NoticeEntity> source = articleId == null
                ? repository.findAll()
                : repository.findByKeyArticleId(articleId);

        return source.stream()
                .filter(entity -> filter == null || entity.getContent().contains(filter))
                .sorted(Comparator.comparing(entity -> entity.getKey().getId()))
                .skip((long) pageNumber * pageSize)
                .limit(pageSize)
                .map(this::toResponse)
                .toList();
    }

    @Override
    public void delete(Long id) {
        NoticeEntity entity = repository.findAll().stream()
                .filter(item -> item.getKey().getId().equals(id))
                .findFirst()
                .orElseThrow(() -> new NotFoundException("Notice not found"));
        repository.deleteById(entity.getKey());
    }

    private long nextId(Long articleId) {
        return repository.findByKeyArticleId(articleId).stream()
                .map(entity -> entity.getKey().getId())
                .max(Long::compareTo)
                .orElse(0L) + 1;
    }

    private NoticeResponseTo toResponse(NoticeEntity entity) {
        return new NoticeResponseTo(entity.getKey().getId(), entity.getKey().getArticleId(), entity.getContent());
    }
}
