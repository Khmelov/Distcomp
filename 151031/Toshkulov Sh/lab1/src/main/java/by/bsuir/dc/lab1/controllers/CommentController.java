package by.bsuir.dc.lab1.controllers;

import by.bsuir.dc.lab1.dto.*;
import by.bsuir.dc.lab1.service.impl.CommentService;
import by.bsuir.dc.lab1.validators.CommentValidator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigInteger;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("api/v1.0/comments")
public class CommentController {

    @Autowired
    private CommentService service;
    @GetMapping(path="/{id}")
    public ResponseEntity<CommentResponseTo> getComment(@PathVariable BigInteger id){
        CommentResponseTo response = service.getById(id);
        if (response != null) {
            return new ResponseEntity<>(response, HttpStatus.OK);
        } else {
            return new ResponseEntity<>(new CommentResponseTo(),HttpStatus.NOT_FOUND);
        }
    }
    @GetMapping
    public ResponseEntity<List<CommentResponseTo>> getComments(){
        List<CommentResponseTo> response = service.getAll();
        if (response != null) {
            return new ResponseEntity<>(response, HttpStatus.OK);
        } else {
            return new ResponseEntity<>(new ArrayList<>(),HttpStatus.NOT_FOUND);
        }
    }
    @PutMapping
    public ResponseEntity<CommentResponseTo> updateComment(@RequestBody CommentRequestTo commentTo){
        CommentResponseTo comment = service.getById(commentTo.getId());
        CommentResponseTo response = null;
        if(comment != null && CommentValidator.validate(commentTo)
           && comment.getId().equals(commentTo.getId())) {
            response = service.update(commentTo);
        }
        if (response != null) {
            return new ResponseEntity<>(response,HttpStatus.OK);
        } else {
            return new ResponseEntity<>(new CommentResponseTo(),HttpStatus.BAD_REQUEST);
        }
    }
    @PostMapping
    public ResponseEntity<CommentResponseTo> addComment(@RequestBody CommentRequestTo commentTo){
        CommentResponseTo response = null;
        if(CommentValidator.validate(commentTo)) {
            response = service.create(commentTo);
        }
        if(response != null){
            return new ResponseEntity<>(response,HttpStatus.CREATED);
        } else {
            return new ResponseEntity<>(new CommentResponseTo(),HttpStatus.BAD_REQUEST);
        }
    }
    @DeleteMapping(path="/{id}")
    public ResponseEntity<CommentResponseTo> deleteComment(@PathVariable BigInteger id){
        boolean isDeleted = service.delete(id);
        if(isDeleted){
            return new ResponseEntity<>(new CommentResponseTo(),HttpStatus.NO_CONTENT);
        } else {
            return new ResponseEntity<>(new CommentResponseTo(),HttpStatus.BAD_REQUEST);
        }
    }
}
