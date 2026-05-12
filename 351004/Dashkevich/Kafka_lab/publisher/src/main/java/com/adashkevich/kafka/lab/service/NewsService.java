package com.adashkevich.kafka.lab.service;

import com.adashkevich.kafka.lab.dto.request.NewsRequestTo;
import com.adashkevich.kafka.lab.dto.response.EditorResponseTo;
import com.adashkevich.kafka.lab.dto.response.MarkerResponseTo;
import com.adashkevich.kafka.lab.dto.response.NewsResponseTo;
import com.adashkevich.kafka.lab.exception.ForbiddenException;
import com.adashkevich.kafka.lab.exception.NotFoundException;
import com.adashkevich.kafka.lab.exception.ValidationException;
import com.adashkevich.kafka.lab.model.News;
import com.adashkevich.kafka.lab.repository.EditorRepository;
import com.adashkevich.kafka.lab.repository.MarkerRepository;
import com.adashkevich.kafka.lab.repository.NewsRepository;
import com.adashkevich.kafka.lab.repository.NewsSpecifications;
import com.adashkevich.kafka.lab.dto.response.MessageResponseTo;
import com.adashkevich.kafka.lab.mapper.EditorMapper;
import com.adashkevich.kafka.lab.mapper.MarkerMapper;
import com.adashkevich.kafka.lab.mapper.NewsMapper;
import com.adashkevich.kafka.lab.model.Editor;
import com.adashkevich.kafka.lab.model.Marker;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.OffsetDateTime;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Service
@Transactional(readOnly = true)
public class NewsService {
    private final NewsRepository newsRepo;
    private final EditorRepository editorRepo;
    private final MarkerRepository markerRepo;
    private final MessageService messageService;
    private final NewsMapper newsMapper;
    private final EditorMapper editorMapper;
    private final MarkerMapper markerMapper;

    public NewsService(NewsRepository newsRepo, EditorRepository editorRepo, MarkerRepository markerRepo,
                       MessageService messageService, NewsMapper newsMapper, EditorMapper editorMapper,
                       MarkerMapper markerMapper) {
        this.newsRepo = newsRepo;
        this.editorRepo = editorRepo;
        this.markerRepo = markerRepo;
        this.messageService = messageService;
        this.newsMapper = newsMapper;
        this.editorMapper = editorMapper;
        this.markerMapper = markerMapper;
    }

    @Transactional
    public NewsResponseTo create(NewsRequestTo dto) {
        ensureTitleUnique(dto.title, null);
        News entity = newsMapper.toEntity(dto);
        entity.setEditor(findEditor(dto.editorId));
        entity.setMarkers(loadOrCreateMarkers(dto.markerIds, dto.markerNames));
        OffsetDateTime now = OffsetDateTime.now();
        entity.setCreated(now);
        entity.setModified(now);
        return newsMapper.toResponse(newsRepo.save(entity));
    }

    public List<NewsResponseTo> getAll() {
        return newsRepo.findAll(Sort.by("id")).stream().map(newsMapper::toResponse).toList();
    }

    public List<NewsResponseTo> search(Collection<Long> markerIds, Collection<String> markerNames,
                                       String editorLogin, String title, String content) {
        Specification<News> spec = Specification.where(NewsSpecifications.markerIdsIn(markerIds))
                .and(NewsSpecifications.markerNamesIn(markerNames))
                .and(NewsSpecifications.editorLoginEquals(editorLogin))
                .and(NewsSpecifications.titleContains(title))
                .and(NewsSpecifications.contentContains(content));
        return newsRepo.findAll(spec, Sort.by("id")).stream().map(newsMapper::toResponse).toList();
    }

    public NewsResponseTo getById(Long id) {
        return newsMapper.toResponse(findExisting(id));
    }

    @Transactional
    public NewsResponseTo update(Long id, NewsRequestTo dto) {
        News existing = findExisting(id);
        ensureTitleUnique(dto.title, id);
        existing.setEditor(findEditor(dto.editorId));
        existing.setTitle(dto.title);
        existing.setContent(dto.content);
        existing.setMarkers(loadOrCreateMarkers(dto.markerIds, dto.markerNames));
        existing.setModified(OffsetDateTime.now());
        return newsMapper.toResponse(newsRepo.save(existing));
    }

    @Transactional
    public void delete(Long id) {
        News news = findExisting(id);

        Set<Marker> markers = new HashSet<>(news.getMarkers());

        messageService.deleteByNewsId(id);

        for (Marker marker : markers) {
            marker.getNews().remove(news);
        }

        news.getMarkers().clear();
        newsRepo.delete(news);

        for (Marker marker : markers) {
            if (marker.getNews() == null || marker.getNews().isEmpty()) {
                markerRepo.delete(marker);
            }
        }
    }

    public EditorResponseTo getEditorByNewsId(Long newsId) {
        return editorMapper.toResponse(findExisting(newsId).getEditor());
    }

    public List<MarkerResponseTo> getMarkersByNewsId(Long newsId) {
        return findExisting(newsId).getMarkers().stream().sorted((a, b) -> a.getId().compareTo(b.getId())).map(markerMapper::toResponse).toList();
    }

    public List<MessageResponseTo> getMessagesByNewsId(Long newsId) {
        if (!newsRepo.existsById(newsId)) throw new NotFoundException("News not found", "40420");
        return messageService.getByNewsId(newsId);
    }

    private News findExisting(Long id) {
        return newsRepo.findById(id).orElseThrow(() -> new NotFoundException("News not found", "40420"));
    }

    private Editor findEditor(Long editorId) {
        return editorRepo.findById(editorId).orElseThrow(() -> new ValidationException("editorId does not exist", "40010"));
    }

    private Set<Marker> loadOrCreateMarkers(Set<Long> markerIds, Set<String> markerNames) {
        Set<Marker> result = new HashSet<>();

        result.addAll(loadMarkersByIds(markerIds));

        if (markerNames != null) {
            for (String name : markerNames) {
                if (name == null || name.isBlank()) {
                    continue;
                }

                String normalizedName = name.trim();

                Marker marker = markerRepo.findByNameIgnoreCase(normalizedName)
                        .orElseGet(() -> {
                            Marker newMarker = new Marker();
                            newMarker.setName(normalizedName);
                            return markerRepo.save(newMarker);
                        });

                result.add(marker);
            }
        }

        return result;
    }

    private Set<Marker> loadMarkersByIds(Set<Long> markerIds) {
        if (markerIds == null || markerIds.isEmpty()) {
            return new HashSet<>();
        }

        List<Marker> markers = markerRepo.findByIdIn(markerIds);

        if (markers.size() != markerIds.size()) {
            for (Long id : markerIds) {
                boolean found = markers.stream().anyMatch(marker -> id.equals(marker.getId()));
                if (!found) {
                    throw new ValidationException("markerId " + id + " does not exist", "40011");
                }
            }
        }

        return new HashSet<>(markers);
    }

    private void ensureTitleUnique(String title, Long selfId) {
        boolean exists = selfId == null ? newsRepo.existsByTitleIgnoreCase(title) : newsRepo.existsByTitleIgnoreCaseAndIdNot(title, selfId);
        if (exists) throw new ForbiddenException("News title must be unique", "40320");
    }
}
