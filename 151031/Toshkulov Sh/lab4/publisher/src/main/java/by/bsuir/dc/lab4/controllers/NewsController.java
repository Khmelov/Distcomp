package by.bsuir.dc.lab4.controllers;

import by.bsuir.dc.lab4.dto.NewsRequestTo;
import by.bsuir.dc.lab4.dto.mappers.NewsMapper;
import by.bsuir.dc.lab4.entities.News;
import by.bsuir.dc.lab4.services.interfaces.NewsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1.0/news")
public class NewsController {
    @Autowired
    private NewsService newsService;

    @GetMapping(path="/{id}")
    public ResponseEntity<News> getById(@PathVariable Long id) {
        News news = newsService.getById(id);
        if(news != null){
            return new ResponseEntity<>(news, HttpStatusCode.valueOf(200));
        } else {
            return new ResponseEntity<>(new News(),HttpStatusCode.valueOf(404));
        }
    }
    @GetMapping
    public ResponseEntity<List<News>> listAll() {
        List<News> news = newsService.getAll();
        return new ResponseEntity<>(news, HttpStatusCode.valueOf(200));
    }

    @PostMapping
    public ResponseEntity<News> add(@RequestBody NewsRequestTo newsRequestTo){
        News news = NewsMapper.instance.convertFromDTO(newsRequestTo);
        News addedNews = newsService.add(news);
        if(addedNews != null) {
            return new ResponseEntity<>(addedNews, HttpStatusCode.valueOf(201));
        } else {
            return new ResponseEntity<>(new News(), HttpStatusCode.valueOf(403));
        }
    }

    @PutMapping
    public ResponseEntity<News> update(@RequestBody NewsRequestTo newsRequestTo){
        News news = NewsMapper.instance.convertFromDTO(newsRequestTo);
        News updatedNews = newsService.update(news);
        if(updatedNews != null) {
            return new ResponseEntity<>(updatedNews, HttpStatusCode.valueOf(200));
        } else {
            return new ResponseEntity<>(new News(), HttpStatusCode.valueOf(403));
        }
    }
    @DeleteMapping(path="/{id}")
    public ResponseEntity<News> delete(@PathVariable Long id){
        News news = newsService.getById(id);
        if(news != null){
            newsService.delete(id);
            return new ResponseEntity<>(news,HttpStatusCode.valueOf(204));
        } else {
            return new ResponseEntity<>(new News(),HttpStatusCode.valueOf(404));
        }
    }
}
