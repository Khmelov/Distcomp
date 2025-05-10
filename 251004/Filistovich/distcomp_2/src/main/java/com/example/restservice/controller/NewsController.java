package com.example.restservice.controller;

import com.example.restservice.dto.MarkerCreateDto;
import com.example.restservice.dto.NewsCreateDto;
import com.example.restservice.dto.NewsResponseDto;
import com.example.restservice.mapper.MarkerMapper;
import com.example.restservice.mapper.NewsMapper;
import com.example.restservice.model.Marker;
import com.example.restservice.model.News;
import com.example.restservice.repository.NewsRepository;
import com.example.restservice.service.AuthorService;
import com.example.restservice.service.MarkerService;
import com.example.restservice.service.NewsService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/v1.0")
@RequiredArgsConstructor
public class NewsController {

    private final NewsMapper newsMapper;
    private final NewsRepository newsRepository;
    private final NewsService newsService;
    private final AuthorService authorService;
    private final MarkerService markerService;
    private final MarkerMapper markerMapper;

    /*@Autowired
    public NewsController(NewsMapper newsMapper, NewsRepository newsRepository, NewsService newsService, AuthorService authorService, MarkerService markerService, MarkerMapper markerMapper) {
        this.newsMapper = newsMapper;
        this.newsRepository = newsRepository;
        this.newsService = newsService;
        this.authorService = authorService;
        this.markerService = markerService;
        this.markerMapper = markerMapper;
    }*/
    
    @GetMapping("/news")
    @ResponseStatus(HttpStatus.OK)
    public List<NewsResponseDto> getAuthors() {
        return newsService.findAll().stream().map(newsMapper::toDto).collect(Collectors.toList());
    }

    @PostMapping("/news")
    @ResponseStatus(HttpStatus.CREATED)
    public ResponseEntity<NewsResponseDto> createAuthor(@RequestBody @Valid NewsCreateDto inputDto) {
        List<Marker> addedMarkers = new ArrayList<>();
        if (inputDto.getMarkers() != null) {
            inputDto.getMarkers().forEach(marker -> {
                MarkerCreateDto m = new MarkerCreateDto();
                m.setName(marker);
                addedMarkers.add(markerService.save(markerMapper.toEntity(m)));
            });
        }
        News entity = newsMapper.toEntity(inputDto, authorService.findById(inputDto.getAuthorId()), addedMarkers);
        if (authorService.findById(inputDto.getAuthorId()) == null) {
            return new ResponseEntity<>(newsMapper.toDto(entity), HttpStatus.NOT_FOUND);
        }
        if (newsRepository.existsByTitle(entity.getTitle())){
            return new ResponseEntity<>(newsMapper.toDto(entity), HttpStatus.FORBIDDEN);
        }
        News saved = newsService.save(entity);
        return new ResponseEntity<>(newsMapper.toDto(saved), HttpStatus.CREATED);
    }

    @GetMapping("/news/{id}")
    public NewsResponseDto getNewsById(@PathVariable Long id) {
        return newsMapper.toDto(newsService.findById(id));
    }

    @PutMapping("/news")
    public ResponseEntity<NewsResponseDto> updateAuthor(@RequestBody @Valid NewsCreateDto in) {
        List<Marker> addedMarkers = new ArrayList<>();
        if (in.getMarkers() != null) {
            in.getMarkers().forEach(marker -> {
                MarkerCreateDto m = new MarkerCreateDto();
                m.setName(marker);
                addedMarkers.add(markerService.save(markerMapper.toEntity(m)));
            });
        }
        News news = newsService.findById(in.getId());
        if (news == null) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        } else {
            News newAuthor = newsMapper.toEntity(in, authorService.findById(in.getAuthorId()), addedMarkers);
            News updated = newsService.update(newAuthor);
            return ResponseEntity.ok(newsMapper.toDto(updated));
        }
    }

    @DeleteMapping("/news/{id}")
    public ResponseEntity<Void> deleteNews(@PathVariable Long id) {
        if (newsService.findById(id) != null) {
            News news = newsService.findById(id);
            List<Marker> markersToDelete = new ArrayList<>(news.getMarkers());

            // Удаляем саму новость (при этом @PreRemove очистит связи)
            newsRepository.delete(news);

            // Удаляем маркеры, которые больше ни с чем не связаны
            markersToDelete.forEach(marker -> {
                if (marker.getNewsList().isEmpty()) {
                    markerService.deleteById(marker.getId());
                }
            });
            newsService.deleteById(id);
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.notFound().build();
    }
}
