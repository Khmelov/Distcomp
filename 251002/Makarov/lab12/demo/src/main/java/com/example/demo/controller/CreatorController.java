package com.example.demo.controller;

import com.example.demo.dto.CreatorRequestTo;
import com.example.demo.dto.CreatorResponseTo;
import com.example.demo.service.CreatorService;
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
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1.0/creators")
public class CreatorController {
    private final CreatorService creatorService;

    @PostMapping
    public ResponseEntity<CreatorResponseTo> createCreator(@Valid @RequestBody CreatorRequestTo request){
        CreatorResponseTo response = new CreatorResponseTo();
        try{
            response = creatorService.createCreator(request);
            return new ResponseEntity<>(response, HttpStatus.CREATED);
        }catch (Exception e){
            return new ResponseEntity<>(response, HttpStatus.FORBIDDEN);
        }

    }

    @GetMapping("/{id}")
    public ResponseEntity<CreatorResponseTo> getCreator(@PathVariable Long id){
        CreatorResponseTo response = creatorService.getCreatorById(id);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @GetMapping
    public ResponseEntity<List<CreatorResponseTo>> getAllCreators(){
        List<CreatorResponseTo> responses = creatorService.getAllCreators();
        return new ResponseEntity<>(responses, HttpStatus.OK);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteCreatorById(@PathVariable Long id){

        try{
            creatorService.deleteCreator(id);
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        }catch (Exception e){
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

    @PutMapping
    public ResponseEntity<CreatorResponseTo> updateCreator(@Valid @RequestBody CreatorRequestTo request){
        CreatorResponseTo response = creatorService.updateCreator(request);
        return response != null
                ? new ResponseEntity<>(response, HttpStatus.OK)
                : new ResponseEntity<>(HttpStatus.NOT_FOUND);
    }
}
