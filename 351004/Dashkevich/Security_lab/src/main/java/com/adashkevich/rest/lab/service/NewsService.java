package com.adashkevich.rest.lab.service;

import com.adashkevich.rest.lab.dto.request.NewsRequestTo;
import com.adashkevich.rest.lab.dto.response.EditorResponseTo;
import com.adashkevich.rest.lab.dto.response.MarkerResponseTo;
import com.adashkevich.rest.lab.dto.response.MessageResponseTo;
import com.adashkevich.rest.lab.dto.response.NewsResponseTo;
import com.adashkevich.rest.lab.exception.ConflictException;
import com.adashkevich.rest.lab.exception.NotFoundException;
import com.adashkevich.rest.lab.exception.ValidationException;
import com.adashkevich.rest.lab.mapper.NewsMapper;
import com.adashkevich.rest.lab.mapper.MarkerMapper;
import com.adashkevich.rest.lab.mapper.MessageMapper;
import com.adashkevich.rest.lab.mapper.EditorMapper;
import com.adashkevich.rest.lab.model.Editor;
import com.adashkevich.rest.lab.model.Marker;
import com.adashkevich.rest.lab.model.Message;
import com.adashkevich.rest.lab.model.News;
import com.adashkevich.rest.lab.repository.EditorRepository;
import com.adashkevich.rest.lab.repository.MarkerRepository;
import com.adashkevich.rest.lab.repository.MessageRepository;
import com.adashkevich.rest.lab.repository.NewsRepository;
import org.springframework.stereotype.Service;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.Set;

@Service
public class NewsService {
    private final NewsRepository newsRepo;
    private final EditorRepository editorRepo;
    private final MarkerRepository markerRepo;
    private final MessageRepository messageRepo;

    private final NewsMapper newsMapper;
    private final EditorMapper editorMapper;
    private final MarkerMapper markerMapper;
    private final MessageMapper messageMapper;

    public NewsService(
            NewsRepository newsRepo,
            EditorRepository editorRepo,
            MarkerRepository markerRepo,
            MessageRepository messageRepo,
            NewsMapper newsMapper,
            EditorMapper editorMapper,
            MarkerMapper markerMapper,
            MessageMapper messageMapper
    ) {
        this.newsRepo = newsRepo;
        this.editorRepo = editorRepo;
        this.markerRepo = markerRepo;
        this.messageRepo = messageRepo;
        this.newsMapper = newsMapper;
        this.editorMapper = editorMapper;
        this.markerMapper = markerMapper;
        this.messageMapper = messageMapper;
    }

    public NewsResponseTo create(NewsRequestTo dto) {
        ensureTitleUnique(dto.title, null);
        ensureEditorExists(dto.editorId);
        ensureMarkersExist(dto.markerIds);

        News entity = newsMapper.toEntity(dto);
        OffsetDateTime now = OffsetDateTime.now();
        entity.setCreated(now);
        entity.setModified(now);
        entity.setMarkerIds(dto.markerIds);

        News saved = newsRepo.save(entity);
        return newsMapper.toResponse(saved);
    }

    public List<NewsResponseTo> getAll() {
        return newsRepo.findAll().stream().map(newsMapper::toResponse).toList();
    }

    public NewsResponseTo getById(Long id) {
        News n = newsRepo.findById(id)
                .orElseThrow(() -> new NotFoundException("News not found", "40420"));
        return newsMapper.toResponse(n);
    }

    public NewsResponseTo update(Long id, NewsRequestTo dto) {
        News existing = newsRepo.findById(id)
                .orElseThrow(() -> new NotFoundException("News not found", "40420"));

        ensureTitleUnique(dto.title, id);
        ensureEditorExists(dto.editorId);
        ensureMarkersExist(dto.markerIds);

        existing.setEditorId(dto.editorId);
        existing.setTitle(dto.title);
        existing.setContent(dto.content);
        existing.setMarkerIds(dto.markerIds);
        existing.setModified(OffsetDateTime.now());

        News updated = newsRepo.update(id, existing);
        return newsMapper.toResponse(updated);
    }

    public void delete(Long id) {
        News news = newsRepo.findById(id)
                .orElseThrow(() -> new NotFoundException("News not found", "40420"));

        for (Message message : messageRepo.findAll()) {
            if (id.equals(message.getNewsId())) {
                messageRepo.deleteById(message.getId());
            }
        }

        newsRepo.deleteById(news.getId());
    }

    public EditorResponseTo getEditorByNewsId(Long newsId) {
        News news = newsRepo.findById(newsId)
                .orElseThrow(() -> new NotFoundException("News not found", "40420"));

        Editor editor = editorRepo.findById(news.getEditorId())
                .orElseThrow(() -> new NotFoundException("Editor not found", "40410"));

        return editorMapper.toResponse(editor);
    }

    public List<MarkerResponseTo> getMarkersByNewsId(Long newsId) {
        News news = newsRepo.findById(newsId)
                .orElseThrow(() -> new NotFoundException("News not found", "40420"));

        Set<Long> ids = news.getMarkerIds();
        return markerRepo.findAll().stream()
                .filter(m -> ids != null && ids.contains(m.getId()))
                .map(markerMapper::toResponse)
                .toList();
    }

    public List<MessageResponseTo> getMessagesByNewsId(Long newsId) {
        if (!newsRepo.existsById(newsId)) {
            throw new NotFoundException("News not found", "40420");
        }
        return messageRepo.findAll().stream()
                .filter(msg -> newsId.equals(msg.getNewsId()))
                .map(messageMapper::toResponse)
                .toList();
    }

    private void ensureEditorExists(Long editorId) {
        if (editorId == null || !editorRepo.existsById(editorId)) {
            throw new ValidationException("editorId does not exist", "40010");
        }
    }

    private void ensureMarkersExist(Set<Long> markerIds) {
        if (markerIds == null) {
            return;
        }

        for (Long id : markerIds) {
            if (id == null || !markerRepo.existsById(id)) {
                throw new ValidationException("markerId " + id + " does not exist", "40011");
            }
        }
    }

    private void ensureTitleUnique(String title, Long selfId) {
        boolean exists = newsRepo.findAll().stream()
                .anyMatch(news ->
                        news.getTitle().equalsIgnoreCase(title)
                                && (selfId == null || !news.getId().equals(selfId)));

        if (exists) {
            throw new ConflictException("News title must be unique", "40920");
        }
    }
}