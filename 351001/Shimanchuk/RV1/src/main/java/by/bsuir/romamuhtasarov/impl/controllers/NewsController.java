package by.bsuir.romamuhtasarov.impl.controllers;

import by.bsuir.romamuhtasarov.impl.service.NewsService;

import by.bsuir.romamuhtasarov.impl.dto.NewsRequestTo;
import by.bsuir.romamuhtasarov.impl.dto.NewsResponseTo;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1.0")
public class NewsController {
    @Autowired
    private NewsService NewsService;

    @GetMapping("/news")
    public ResponseEntity<List<NewsResponseTo>> getAllNews() {
        List<NewsResponseTo> NewsResponseToList = NewsService.getAll();
        return new ResponseEntity<>(NewsResponseToList, HttpStatus.OK);
    }

    @GetMapping("/news/{id}")
    public ResponseEntity<NewsResponseTo> getNews(@PathVariable long id) {
        NewsResponseTo NewsResponseTo = NewsService.get(id);
        return new ResponseEntity<>(NewsResponseTo, NewsResponseTo == null ? HttpStatus.NOT_FOUND : HttpStatus.OK);
    }

    @PostMapping("/news")
    public ResponseEntity<NewsResponseTo> createNews(@RequestBody NewsRequestTo NewsTo) {
        NewsResponseTo addedNews = NewsService.add(NewsTo);
        return new ResponseEntity<>(addedNews, HttpStatus.CREATED);
    }

    @DeleteMapping("/news/{id}")
    public ResponseEntity<NewsResponseTo> deleteNews(@PathVariable long id) {
        NewsResponseTo deletedNews = NewsService.delete(id);
        return new ResponseEntity<>(deletedNews, deletedNews == null ? HttpStatus.NOT_FOUND : HttpStatus.NO_CONTENT);
    }

    @PutMapping("/news")
    public ResponseEntity<NewsResponseTo> updateNews(@RequestBody NewsRequestTo NewsRequestTo) {
        NewsResponseTo NewsResponseTo = NewsService.update(NewsRequestTo);
        return new ResponseEntity<>(NewsResponseTo, NewsResponseTo.getContent() == null ? HttpStatus.NOT_FOUND : HttpStatus.OK);
    }
}