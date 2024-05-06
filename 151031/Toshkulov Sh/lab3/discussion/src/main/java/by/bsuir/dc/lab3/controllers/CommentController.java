package by.bsuir.dc.lab3.controllers;

import by.bsuir.dc.lab3.dto.CommentRequestTo;
import by.bsuir.dc.lab3.dto.mappers.CommentMapper;
import by.bsuir.dc.lab3.entities.Comment;
import by.bsuir.dc.lab3.services.interfaces.CommentDiscService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@CrossOrigin(maxAge = 3600)
@RestController
@RequestMapping("/api/v1.0/comments")
public class CommentController {
    @Autowired
    private CommentDiscService commentService;

    @GetMapping(path="/{id}")
    public ResponseEntity<Comment> getById(@PathVariable Long id) {
        Comment comment = commentService.getById(id);
        if(comment != null){
            return new ResponseEntity<>(comment, HttpStatusCode.valueOf(200));
        } else {
            return new ResponseEntity<>(new Comment(),HttpStatusCode.valueOf(404));
        }
    }
    @GetMapping
    public ResponseEntity<List<Comment>> listAll() {
        List<Comment> comment = commentService.getAll();
        return new ResponseEntity<>(comment, HttpStatusCode.valueOf(200));
    }

    @PostMapping
    public ResponseEntity<Comment> add(@RequestBody CommentRequestTo commentRequestTo){
        commentRequestTo.setId(Math.abs(UUID.randomUUID().getLeastSignificantBits()) % 1000000);
        Comment comment = CommentMapper.instance.convertFromDTO(commentRequestTo);
        Comment addedComment = commentService.add(comment);
        if(addedComment != null) {
            return new ResponseEntity<>(addedComment, HttpStatusCode.valueOf(201));
        } else {
            return new ResponseEntity<>(new Comment(), HttpStatusCode.valueOf(403));
        }
    }

    @PutMapping
    public ResponseEntity<Comment> update(@RequestBody CommentRequestTo commentRequestTo){
        Comment comment = CommentMapper.instance.convertFromDTO(commentRequestTo);
        Comment updatedComment = commentService.update(comment);
        if(updatedComment != null) {
            return new ResponseEntity<>(updatedComment, HttpStatusCode.valueOf(200));
        } else {
            return new ResponseEntity<>(new Comment(), HttpStatusCode.valueOf(403));
        }
    }
    @DeleteMapping(path="/{id}")
    public ResponseEntity<Comment> delete(@PathVariable Long id){
        Comment comment = commentService.getById(id);
        if(comment != null){
            commentService.delete(id);
            return new ResponseEntity<>(comment,HttpStatusCode.valueOf(204));
        } else {
            return new ResponseEntity<>(new Comment(),HttpStatusCode.valueOf(404));
        }
    }
}
