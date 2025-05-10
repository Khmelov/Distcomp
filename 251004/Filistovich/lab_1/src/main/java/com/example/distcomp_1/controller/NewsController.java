package com.example.distcomp_1.controller;

import com.example.distcomp_1.mapper.AuthorDto;
import com.example.distcomp_1.mapper.NewsDto;
import com.example.distcomp_1.mdoel.Author;
import com.example.distcomp_1.mdoel.News;
import com.example.distcomp_1.repository.AuthorRepository;
import com.example.distcomp_1.repository.NewsRepository;
import com.example.distcomp_1.service.AuthorService;
import com.example.distcomp_1.service.NewsService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
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

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/v1.0")
public class NewsController {

    private final NewsDto newsDto;
    private final NewsRepository newsRepository;
    private final NewsService newsService;

    @Autowired
    public NewsController(NewsDto newsDto, NewsRepository newsRepository, NewsService newsService) {
        this.newsDto = newsDto;
        this.newsRepository = newsRepository;
        this.newsService = newsService;
    }
    
    @GetMapping("/news")
    @ResponseStatus(HttpStatus.OK)
    public List<News.Out> getAuthors() {
        return newsRepository.getNews()
                .stream()
                .map(newsDto::Out)
                .collect(Collectors.toList());
    }

    @PostMapping("/news")
    @ResponseStatus(HttpStatus.CREATED)
    public News.Out createAuthor(@RequestBody @Valid News.In inputDto) {
        News entity = newsDto.In(inputDto);
        News saved = newsService.save(entity);
        return newsDto.Out(saved);
    }

    @GetMapping("/news/{id}")
    public News.Out getAuthorById(@PathVariable Long id) {
        return newsDto.Out(newsService.findById(id));
    }

    @PutMapping("/news")
    public ResponseEntity<News.Out> updateAuthor(@RequestBody @Valid News.In in) {
        News news = newsService.findById(in.getId());
        if (news == null) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        } else {
            News newAuthor = newsDto.In(in);
            News updated = newsService.update(newAuthor);
            return ResponseEntity.ok(newsDto.Out(updated));
        }
    }

    @DeleteMapping("/news/{id}")
    public ResponseEntity<Void> deleteAuthor(@PathVariable Long id) {
        if (newsService.findById(id) != null) {
            newsService.deleteById(id);
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.notFound().build();
    }
}
