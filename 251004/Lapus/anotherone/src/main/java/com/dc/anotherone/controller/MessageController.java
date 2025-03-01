package com.dc.anotherone.controller;

import com.dc.anotherone.exception.ServiceException;
import com.dc.anotherone.mapper.MessageMapper;
import com.dc.anotherone.model.blo.Message;
import com.dc.anotherone.model.blo.News;
import com.dc.anotherone.model.dto.MessageRequestTo;
import com.dc.anotherone.model.dto.MessageResponseTo;
import com.dc.anotherone.service.MessageService;
import com.dc.anotherone.service.NewsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping(path = "/api/v1.0")
public class MessageController {

    @Autowired
    private MessageMapper mapper;

    @Autowired
    private MessageService service;

    @Autowired
    private NewsService newsService;

    @GetMapping(path = "/messages", produces = MediaType.APPLICATION_JSON_VALUE)
    ResponseEntity<List<MessageResponseTo>> getAll() {
        return ResponseEntity.ok(List.copyOf(mapper.mapToListDto(service.getAll())));
    }

    @PostMapping(path = "/messages", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    ResponseEntity<MessageResponseTo> save(@RequestBody MessageRequestTo MessageRequestTo){
        Message buf = mapper.mapToBlo(MessageRequestTo);
        buf.setNews(newsService.getById(MessageRequestTo.getNewsId()));
        Message Message = service.save(buf);
        if(Message==null)
        {
            throw new ServiceException("Incorrect user", 404);
        }
        else{
            return ResponseEntity.status(HttpStatus.CREATED).body(mapper.mapToDto(Message));
        }
    }

    @DeleteMapping(path = "/messages/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
    ResponseEntity<Void> delete(@PathVariable(name = "id") Long id){
        service.delete(id);
        HttpStatus status = HttpStatus.NO_CONTENT;
        return ResponseEntity.status(status).build();
    }

    @GetMapping(path = "/messages/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
    ResponseEntity<MessageResponseTo> get(@PathVariable(name = "id") Long id) {
        return ResponseEntity.ok(mapper.mapToDto(service.getById(id)));
    }

    @PutMapping(path = "/messages", produces = MediaType.APPLICATION_JSON_VALUE)
    ResponseEntity<MessageResponseTo> update(@RequestBody MessageRequestTo MessageRequestTo) {
        Message buf = mapper.mapToBlo(MessageRequestTo);
        buf.setNews(newsService.getById(MessageRequestTo.getNewsId()));
        Message Message = service.update(buf);
        if(Message==null)
        {
            throw new ServiceException("Incorrect user", 404);
        }
        else{
            return ResponseEntity.ok(mapper.mapToDto(Message));
        }
    }

}
