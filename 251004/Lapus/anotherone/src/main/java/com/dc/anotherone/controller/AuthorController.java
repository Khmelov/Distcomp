package com.dc.anotherone.controller;

import com.dc.anotherone.exception.ServiceException;
import com.dc.anotherone.mapper.AuthorMapper;
import com.dc.anotherone.model.blo.Author;
import com.dc.anotherone.model.dto.AuthorRequestTo;
import com.dc.anotherone.model.dto.AuthorResponseTo;
import com.dc.anotherone.service.AuthorService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping(path = "/api/v1.0")
public class AuthorController {

    private final AuthorMapper mapper;

    private final AuthorService service;

    @Autowired
    public AuthorController(AuthorMapper mapper, AuthorService service) {
        this.mapper = mapper;
        this.service = service;
    }

    @GetMapping(path = "/authors", produces = MediaType.APPLICATION_JSON_VALUE)
    ResponseEntity<List<AuthorResponseTo>> getAll() {
        return ResponseEntity.ok(List.copyOf(mapper.mapToListDto(service.getAll())));
    }

    @PostMapping(path = "/authors", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    ResponseEntity<AuthorResponseTo> save(@RequestBody AuthorRequestTo authorRequestTo){
        Author author = service.save(mapper.mapToBlo(authorRequestTo));
        if(author==null)
        {
            throw new ServiceException("Incorrect user", 404);
        }
        else{
            return ResponseEntity.status(HttpStatus.CREATED).body(mapper.mapToDto(author));
        }
    }

    @DeleteMapping(path = "/authors/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
    ResponseEntity<Void> delete(@PathVariable(name = "id") Long id){
        service.delete(id);
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }

    @GetMapping(path = "/authors/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
    ResponseEntity<AuthorResponseTo> get(@PathVariable(name = "id") Long id) {
        return ResponseEntity.ok(mapper.mapToDto(service.getById(id)));
    }

    @PutMapping(path = "/authors", produces = MediaType.APPLICATION_JSON_VALUE)
    ResponseEntity<AuthorResponseTo> update(@RequestBody AuthorRequestTo authorRequestTo) {
        Author buf = mapper.mapToBlo(authorRequestTo);
        Author author = service.update(buf);
        if(author==null)
        {
            throw new ServiceException("Incorrect user", 404);
        }
        else{
            return ResponseEntity.ok(mapper.mapToDto(author));
        }
    }

}
