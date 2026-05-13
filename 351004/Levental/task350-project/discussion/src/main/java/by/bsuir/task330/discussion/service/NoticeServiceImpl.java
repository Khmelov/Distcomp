package by.bsuir.task330.discussion.service;

import by.bsuir.task330.discussion.dto.NoticeRequestTo;
import by.bsuir.task330.discussion.dto.NoticeResponseTo;
import by.bsuir.task330.discussion.entity.NoticeEntity;
import by.bsuir.task330.discussion.entity.NoticeKey;
import by.bsuir.task330.discussion.repository.NoticeRepository;
import by.bsuir.task330.discussion.service.NoticeService;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class NoticeServiceImpl implements NoticeService {

    private final NoticeRepository repository;

    public NoticeServiceImpl(NoticeRepository repository) {
        this.repository = repository;
    }

    @Override
    public NoticeResponseTo create(NoticeRequestTo request) {

        Long generatedId = System.currentTimeMillis();

        NoticeKey key = new NoticeKey(
                request.articleId(),
                generatedId
        );

        NoticeEntity entity = new NoticeEntity();
        entity.setKey(key);
        entity.setContent(request.content());

        NoticeEntity saved = repository.save(entity);

        return map(saved);
    }
    @Override
    public NoticeResponseTo update(NoticeRequestTo request) {

        NoticeKey key = new NoticeKey(
                request.articleId(),
                request.id()
        );

        NoticeEntity entity = repository.findById(key)
                .orElseThrow();

        entity.setContent(request.content());

        NoticeEntity updated = repository.save(entity);

        return map(updated);
    }

    @Override
    public NoticeResponseTo findById(Long id) {

        NoticeEntity entity = repository.findAll()
                .stream()
                .filter(n -> n.getKey().getId().equals(id))
                .findFirst()
                .orElseThrow();

        return map(entity);
    }

    @Override
    public List<NoticeResponseTo> findAll(Integer page, Integer size, String sort, String filter, Long articleId) {
        return repository.findAll()
                .stream()
                .map(this::map)
                .toList();
    }

    @Override
    public void delete(Long id) {

        NoticeEntity entity = repository.findAll()
                .stream()
                .filter(n -> n.getKey().getId().equals(id))
                .findFirst()
                .orElseThrow();

        repository.delete(entity);
    }

    private NoticeResponseTo map(NoticeEntity entity) {
        return new NoticeResponseTo(
                entity.getKey().getId(),
                entity.getKey().getArticleId(),
                entity.getContent()
        );
    }
}