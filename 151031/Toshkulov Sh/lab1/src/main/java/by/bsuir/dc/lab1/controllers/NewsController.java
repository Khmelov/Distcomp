package by.bsuir.dc.lab1.controllers;

import by.bsuir.dc.lab1.dto.NewsRequestTo;
import by.bsuir.dc.lab1.dto.NewsResponseTo;
import by.bsuir.dc.lab1.entities.News;
import by.bsuir.dc.lab1.service.impl.NewsService;
import by.bsuir.dc.lab1.validators.NewsValidator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("api/v1.0/news")
public class NewsController {
    @Autowired
    private NewsService service;
    @GetMapping(path="/{id}")
    public ResponseEntity<NewsResponseTo> getNews(@PathVariable BigInteger id){

        NewsResponseTo response = service.getById(id);
        if (response != null) {
            return new ResponseEntity<>(response, HttpStatus.OK);
        } else {
            return new ResponseEntity<>(new NewsResponseTo(),HttpStatus.NOT_FOUND);
        }
    }
    @GetMapping
    public ResponseEntity<List<NewsResponseTo>> getMarkers() {
        List<NewsResponseTo> response = service.getAll();
        if (response != null) {
            return new ResponseEntity<>(response,HttpStatus.OK);
        } else {
            return new ResponseEntity<>(new ArrayList<>(),HttpStatus.NOT_FOUND);
        }
    }
    @PutMapping
    public ResponseEntity<NewsResponseTo> updateNews(@RequestBody NewsRequestTo newsTo){

        NewsResponseTo news = service.getById(newsTo.getId());
        NewsResponseTo response = null;
        if(news != null && NewsValidator.validate(newsTo)&& news.getId().equals(newsTo.getId())){
            response = service.update(newsTo);
        }
        if (response != null) {
            return new ResponseEntity<>(response,HttpStatus.OK);
        } else {
            return new ResponseEntity<>(new NewsResponseTo(),HttpStatus.BAD_REQUEST);
        }
    }
    @PostMapping
    public ResponseEntity<NewsResponseTo> addNews(@RequestBody NewsRequestTo newsTo){

        NewsResponseTo response = null;
        if(NewsValidator.validate(newsTo)) {
            response = service.create(newsTo);
        }
        if(response != null){
            return new ResponseEntity<>(response,HttpStatus.CREATED);
        } else {
            return new ResponseEntity<>(new NewsResponseTo(),HttpStatus.BAD_REQUEST);
        }
    }
    @DeleteMapping(path="/{id}")
    public ResponseEntity<NewsResponseTo> deleteNews(@PathVariable BigInteger id){
        boolean isDeleted = service.delete(id);
        if(isDeleted){
            return new ResponseEntity<>(new NewsResponseTo(),HttpStatus.NO_CONTENT);
        } else {
            return new ResponseEntity<>(new NewsResponseTo(),HttpStatus.BAD_REQUEST);
        }
    }
}
