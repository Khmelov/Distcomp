package by.bsuir.romamuhtasarov.impl.controllers;

import by.bsuir.romamuhtasarov.impl.service.CommentService;
import by.bsuir.romamuhtasarov.impl.dto.CommentResponseTo;
import by.bsuir.romamuhtasarov.impl.dto.CommentRequestTo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1.0")
public class CommentController {
    @Autowired
    private CommentService commentService;

    @GetMapping("/comments")
    public ResponseEntity<List<CommentResponseTo>> getAllComments() {
        List<CommentResponseTo> commentResponseToList = commentService.getAll();
        return new ResponseEntity<>(commentResponseToList, HttpStatus.OK);
    }

    @GetMapping("/comments/{id}")
    public ResponseEntity<CommentResponseTo> getComment(@PathVariable long id) {
        CommentResponseTo CommentResponseTo = commentService.get(id);
        return new ResponseEntity<>(CommentResponseTo, CommentResponseTo == null ? HttpStatus.NOT_FOUND : HttpStatus.OK);
    }

    @PostMapping("/comments")
    public ResponseEntity<CommentResponseTo> createComment(@RequestBody CommentRequestTo CommentTo) {
        CommentResponseTo addedComment = commentService.add(CommentTo);
        return new ResponseEntity<>(addedComment, HttpStatus.CREATED);
    }

    @DeleteMapping("/comments/{id}")
    public ResponseEntity<CommentResponseTo> deleteComment(@PathVariable long id) {
        CommentResponseTo deletedComment = commentService.delete(id);
        return new ResponseEntity<>(deletedComment, deletedComment == null ? HttpStatus.NOT_FOUND : HttpStatus.NO_CONTENT);
    }

    @PutMapping("/comments")
    public ResponseEntity<CommentResponseTo> updateComment(@RequestBody CommentRequestTo CommentRequestTo) {
        CommentResponseTo CommentResponseTo = commentService.update(CommentRequestTo);
        return new ResponseEntity<>(CommentResponseTo, CommentResponseTo.getContent() == null ? HttpStatus.NOT_FOUND : HttpStatus.OK);
    }
}