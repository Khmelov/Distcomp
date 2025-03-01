package com.dc.anotherone.controller;

import com.dc.anotherone.exception.ServiceException;
import com.dc.anotherone.mapper.MarkerMapper;
import com.dc.anotherone.model.blo.Marker;
import com.dc.anotherone.model.dto.MarkerRequestTo;
import com.dc.anotherone.model.dto.MarkerResponseTo;
import com.dc.anotherone.service.MarkerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping(path = "/api/v1.0")
public class MarkerController {

    private final MarkerMapper mapper;

    private final MarkerService service;

    @Autowired
    public MarkerController(MarkerMapper mapper, MarkerService service) {
        this.mapper = mapper;
        this.service = service;
    }

    @GetMapping(path = "/markers", produces = MediaType.APPLICATION_JSON_VALUE)
    ResponseEntity<List<MarkerResponseTo>> getAll() {
        return ResponseEntity.ok(List.copyOf(mapper.mapToListDto(service.getAll())));
    }

    @PostMapping(path = "/markers", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    ResponseEntity<MarkerResponseTo> save(@RequestBody MarkerRequestTo MarkerRequestTo){
        Marker Marker = service.save(mapper.mapToBlo(MarkerRequestTo));
        if(Marker==null)
        {
            throw new ServiceException("Incorrect user", 404);
        }
        else{
            return ResponseEntity.status(HttpStatus.CREATED).body(mapper.mapToDto(Marker));
        }
    }

    @DeleteMapping(path = "/markers/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
    ResponseEntity<Void> delete(@PathVariable(name = "id") Long id){
        service.delete(id);
        HttpStatus status = HttpStatus.NO_CONTENT;
        return ResponseEntity.status(status).build();
    }

    @GetMapping(path = "/markers/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
    ResponseEntity<MarkerResponseTo> get(@PathVariable(name = "id") Long id) {
        return ResponseEntity.ok(mapper.mapToDto(service.getById(id)));
    }

    @PutMapping(path = "/markers", produces = MediaType.APPLICATION_JSON_VALUE)
    ResponseEntity<MarkerResponseTo> update(@RequestBody MarkerRequestTo MarkerRequestTo) {
        Marker buf = mapper.mapToBlo(MarkerRequestTo);
        Marker Marker = service.update(buf);
        if(Marker==null)
        {
            throw new ServiceException("Incorrect user", 404);
        }
        else{
            return ResponseEntity.ok(mapper.mapToDto(Marker));
        }
    }

}
