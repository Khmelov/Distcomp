package com.dc.anotherone.controller;

import com.dc.anotherone.exception.ServiceException;
import com.dc.anotherone.mapper.NewsMapper;
import com.dc.anotherone.model.blo.News;
import com.dc.anotherone.model.dto.NewsRequestTo;
import com.dc.anotherone.model.dto.NewsResponseTo;
import com.dc.anotherone.service.AuthorService;
import com.dc.anotherone.service.NewsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping(path = "/api/v1.0")
public class NewsController {

    @Autowired
    private NewsMapper mapper;

    @Autowired
    private NewsService service;

    @Autowired
    private AuthorService authorService;

    @GetMapping(path = "/news", produces = MediaType.APPLICATION_JSON_VALUE)
    ResponseEntity<List<NewsResponseTo>> getAll() {
        return ResponseEntity.ok(List.copyOf(mapper.mapToListDto(service.getAll())));
    }

    @PostMapping(path = "/news", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    ResponseEntity<NewsResponseTo> save(@RequestBody NewsRequestTo NewsRequestTo){
        News buf = mapper.mapToBlo(NewsRequestTo);
        buf.setAuthor(authorService.getById(NewsRequestTo.getAuthorId()));
        News News = service.save(buf);
        if(News==null)
        {
            throw new ServiceException("Incorrect user", 404);
        }
        else{
            return ResponseEntity.status(HttpStatus.CREATED).body(mapper.mapToDto(News));
        }
    }

    @DeleteMapping(path = "/news/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
    ResponseEntity<Void> delete(@PathVariable(name = "id") Long id){
        service.delete(id);
        HttpStatus status = HttpStatus.NO_CONTENT;
        return ResponseEntity.status(status).build();
    }

    @GetMapping(path = "/news/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
    ResponseEntity<NewsResponseTo> get(@PathVariable(name = "id") Long id) {
        return ResponseEntity.ok(mapper.mapToDto(service.getById(id)));
    }

    @PutMapping(path = "/news", produces = MediaType.APPLICATION_JSON_VALUE)
    ResponseEntity<NewsResponseTo> update(@RequestBody NewsRequestTo NewsRequestTo) {
        News buf = mapper.mapToBlo(NewsRequestTo);
        buf.setAuthor(authorService.getById(NewsRequestTo.getAuthorId()));
        buf.setCreated(service.getById(NewsRequestTo.getId()).getCreated());
        News News = service.update(buf);
        if(News==null)
        {
            throw new ServiceException("Incorrect user", 404);
        }
        else{
            return ResponseEntity.ok(mapper.mapToDto(News));
        }
    }

}
