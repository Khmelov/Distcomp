package com.bsuir.romanmuhtasarov.controllers;

import com.bsuir.romanmuhtasarov.domain.request.NewsRequestTo;
import com.bsuir.romanmuhtasarov.domain.response.NewsResponseTo;
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
import org.springframework.web.bind.annotation.RestController;
import com.bsuir.romanmuhtasarov.serivces.NewsService;

import java.util.List;

@RestController
@RequestMapping("/news")
public class NewsController {
    private final NewsService newsService;

    @Autowired
    public NewsController(NewsService newsService) {
        this.newsService = newsService;
    }

    @PostMapping
    public ResponseEntity<NewsResponseTo> createNews(@RequestBody NewsRequestTo newsRequestTo) {
        return ResponseEntity.status(HttpStatus.CREATED).body(newsService.create(newsRequestTo));
    }

    @GetMapping("/{id}")
    public ResponseEntity<NewsResponseTo> findNewsById(@PathVariable Long id) {
        return ResponseEntity.status(HttpStatus.OK).body(newsService.findNewsById(id));
    }

    @GetMapping
    public ResponseEntity<List<NewsResponseTo>> findAllNewss() {
        return ResponseEntity.status(HttpStatus.OK).body(newsService.read());
    }

    @PutMapping
    public ResponseEntity<NewsResponseTo> updateNews(@RequestBody NewsRequestTo newsRequestTo) {
        return ResponseEntity.status(HttpStatus.OK).body(newsService.update(newsRequestTo));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Long> deleteNewsById(@PathVariable Long id) {
        newsService.delete(id);
        return ResponseEntity.status(HttpStatus.NO_CONTENT).body(id);
    }
}
