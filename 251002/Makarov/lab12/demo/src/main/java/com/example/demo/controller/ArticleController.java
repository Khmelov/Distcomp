package com.example.demo.controller;

import com.example.demo.dto.ArticleRequestTo;
import com.example.demo.dto.ArticleResponseTo;
import com.example.demo.dto.CreatorResponseTo;
import com.example.demo.model.Article;
import com.example.demo.service.ArticleService;
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
@RequestMapping("/api/v1.0/articles")
public class ArticleController {
    private final ArticleService service;

    @PostMapping
    public ResponseEntity<ArticleResponseTo> createArticle(@Valid @RequestBody ArticleRequestTo request){
        ArticleResponseTo response = new ArticleResponseTo();
        try{
            response = service.createArticle(request);
            return new ResponseEntity<>(response, HttpStatus.CREATED);
        }catch (Exception e){
            System.out.println("ПИДОРЫ "+e.getMessage());
            return new ResponseEntity<>(response, HttpStatus.FORBIDDEN);
        }
    }

    @GetMapping("/{id}")
    public ResponseEntity<ArticleResponseTo> getArticle(@PathVariable Long id){
        ArticleResponseTo response = service.getArticleById(id);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }
    @GetMapping
    public ResponseEntity<List<ArticleResponseTo>> getAllArticles(){
        List<ArticleResponseTo> responses = service.getAllArticles();
        return new ResponseEntity<>(responses, HttpStatus.OK);
    }
    @PutMapping()
    public ResponseEntity<ArticleResponseTo> updateArticle(@Valid @RequestBody ArticleRequestTo request){
        ArticleResponseTo response = service.updateArticle(request);
        return response != null
                ? new ResponseEntity<>(response, HttpStatus.OK)
                : new ResponseEntity<>(HttpStatus.NOT_FOUND);
    }
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteNoteById(@PathVariable Long id){

        try{
            service.deleteArticle(id);
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        }catch (Exception e){
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }
}
