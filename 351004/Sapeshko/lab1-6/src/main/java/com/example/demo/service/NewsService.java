package com.example.demo.service;

import com.example.demo.dto.NewsRequest;
import com.example.demo.entity.Author;
import com.example.demo.entity.News;
import com.example.demo.entity.Tag;
import com.example.demo.exception.NotFoundException;
import com.example.demo.repository.AuthorRepository;
import com.example.demo.repository.NewsRepository;
import com.example.demo.repository.TagRepository;
import com.example.demo.sync.SyncService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.context.annotation.Profile;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Profile("docker")
@Service
public class NewsService extends AbstractGenericService<News, Long> {
    private final AuthorRepository authorRepository;
    private final TagRepository tagRepository;
    @Autowired
    private SyncService syncService;

    public NewsService(NewsRepository repository,
                       AuthorRepository authorRepository,
                       TagRepository tagRepository) {
        super(repository, repository);
        this.authorRepository = authorRepository;
        this.tagRepository = tagRepository;
    }

    @Transactional
    public News createFromRequest(NewsRequest request) {
        Author author = authorRepository.findById(request.getAuthorId())
                .orElseThrow(() -> new NotFoundException("Author not found with id: " + request.getAuthorId()));

        News news = new News();
        news.setAuthor(author);
        news.setTitle(request.getTitle());
        news.setContent(request.getContent());
        news.setCreated(LocalDateTime.now());
        news.setModified(LocalDateTime.now());

        if (request.getTags() != null && !request.getTags().isEmpty()) {
            List<Tag> tags = new ArrayList<>();
            for (String tagName : request.getTags()) {
                Tag tag = tagRepository.findByName(tagName)
                        .orElseGet(() -> {
                            Tag newTag = new Tag();
                            newTag.setName(tagName);
                            return tagRepository.save(newTag);
                        });
                tags.add(tag);
            }
            news.setTags(tags);
        }

        News saved = repository.save(news);
        syncService.syncNews(saved, "create");
        return saved;
    }

    @Transactional
    public News updateFromRequest(Long id, NewsRequest request) {
        News news = repository.findById(id)
                .orElseThrow(() -> new NotFoundException("News not found with id: " + id));
        Author author = authorRepository.findById(request.getAuthorId())
                .orElseThrow(() -> new NotFoundException("Author not found with id: " + request.getAuthorId()));

        news.setAuthor(author);
        news.setTitle(request.getTitle());
        news.setContent(request.getContent());
        news.setModified(LocalDateTime.now());

        if (request.getTags() != null) {
            List<Tag> tags = new ArrayList<>();
            for (String tagName : request.getTags()) {
                Tag tag = tagRepository.findByName(tagName)
                        .orElseGet(() -> {
                            Tag newTag = new Tag();
                            newTag.setName(tagName);
                            return tagRepository.save(newTag);
                        });
                tags.add(tag);
            }
            news.setTags(tags);
        }

        News updated = repository.save(news);
        syncService.syncNews(updated, "update");
        return updated;
    }

    @Override
    @Transactional
    public void delete(Long id) {
        News news = repository.findById(id)
                .orElseThrow(() -> new NotFoundException("News not found with id: " + id));
        List<Tag> tags = new ArrayList<>(news.getTags());
        repository.delete(news);
        for (Tag tag : tags) {
            boolean usedElsewhere = tag.getNews().stream().anyMatch(n -> !n.getId().equals(id));
            if (!usedElsewhere) {
                tagRepository.delete(tag);
            }
        }
        syncService.syncNews(news, "delete");
    }
}