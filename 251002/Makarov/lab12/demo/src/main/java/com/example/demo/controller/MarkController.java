package com.example.demo.controller;

import com.example.demo.dto.CreatorRequestTo;
import com.example.demo.dto.CreatorResponseTo;
import com.example.demo.dto.MarkRequestTo;
import com.example.demo.dto.MarkResponseTo;
import com.example.demo.service.MarkService;
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
@RequestMapping("/api/v1.0/marks")
public class MarkController {
    private final MarkService service;

    @PostMapping
    public ResponseEntity<MarkResponseTo> createMark(@Valid @RequestBody MarkRequestTo request){
        MarkResponseTo response = service.createMark(request);
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    @GetMapping("/{id}")
    public ResponseEntity<MarkResponseTo> getMark(@PathVariable Long id){
        MarkResponseTo response = service.getMarkById(id);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }
    @PutMapping
    public ResponseEntity<MarkResponseTo> updateMark(@Valid @RequestBody MarkRequestTo request){
        MarkResponseTo response = service.updateMark(request);
        return response != null
                ? new ResponseEntity<>(response, HttpStatus.OK)
                : new ResponseEntity<>(HttpStatus.NOT_FOUND);
    }
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteMarkById(@PathVariable Long id){

        try{
            service.deleteMark(id);
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        }catch (Exception e){
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }
    @GetMapping
    public ResponseEntity<List<MarkResponseTo>> getAllMarks(){
        List<MarkResponseTo> responses = service.getAllMarks();
        return new ResponseEntity<>(responses, HttpStatus.OK);
    }
}
