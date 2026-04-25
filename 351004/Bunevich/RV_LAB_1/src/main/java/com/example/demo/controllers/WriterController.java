package com.example.demo.controllers;

import com.example.demo.dto.request.WriterRequestTo;
import com.example.demo.dto.response.WriterResponseTo;
import com.example.demo.servises.WriterServise;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1.0/writers")
@RequiredArgsConstructor
@Slf4j
public class WriterController {
    private final WriterServise writerService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public WriterResponseTo createWriter(@Valid @RequestBody WriterRequestTo writerRequest){
        log.info("REST request to create writer: {}", writerRequest);
        return writerService.create(writerRequest);
    }

    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    public List<WriterResponseTo> getAllWriters(){
        return writerService.findAll();
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteWriter(@PathVariable Long id){
        writerService.delete(id);
    }
    @GetMapping("/{id}")
    @ResponseStatus(HttpStatus.OK)
    public WriterResponseTo getWriterByid(@PathVariable Long id){
        return writerService.findById(id);
    }
    @PutMapping("/{id}")
    @ResponseStatus(HttpStatus.OK)
    public WriterResponseTo updateWriter(@PathVariable Long id, @Valid @RequestBody WriterRequestTo writerRequestTo){
        return writerService.update(id, writerRequestTo);
    }
}
