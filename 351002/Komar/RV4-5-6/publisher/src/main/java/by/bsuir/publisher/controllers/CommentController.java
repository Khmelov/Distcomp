package by.bsuir.publisher.controllers;

import by.bsuir.publisher.dto.requests.CommentRequestDto;
import by.bsuir.publisher.dto.responses.CommentResponseDto;
import by.bsuir.publisher.exceptions.EntityExistsException;
import by.bsuir.publisher.exceptions.Comments;
import by.bsuir.publisher.exceptions.NoEntityExistsException;
import by.bsuir.publisher.exceptions.ServiceException;
import by.bsuir.publisher.services.CommentService;
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
@RequestMapping("/comments")
@RequiredArgsConstructor
public class CommentController {

    private final CommentService commentService;

    @PostMapping
    public ResponseEntity<CommentResponseDto> create(@RequestBody CommentRequestDto comment) throws ServiceException {
        CommentResponseDto mes = commentService.create(comment);
        if (mes != null) {
            return ResponseEntity.status(HttpStatus.CREATED).body(mes);
        } else {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new CommentResponseDto());
        }
    }

    @GetMapping("/{id}")
    public ResponseEntity<CommentResponseDto> read(@PathVariable("id") Long id) throws NoEntityExistsException, ServiceException {
        return ResponseEntity.status(HttpStatus.OK).body(commentService.read(id).orElseThrow(() ->
                new NoEntityExistsException(Comments.NoEntityExistsException)));
    }

    @GetMapping
    public ResponseEntity<List<CommentResponseDto>> read() throws ServiceException {
        return ResponseEntity.status(HttpStatus.OK).body(commentService.readAll());
    }

    @PutMapping
    public ResponseEntity<CommentResponseDto> update(@RequestBody CommentRequestDto comment) throws ServiceException {
        return ResponseEntity.status(HttpStatus.OK).body(commentService.update(comment));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Long> delete(@PathVariable("id") Long id) throws ServiceException {
        Long o = commentService.delete(id);
        if (o != -1L) {
            return ResponseEntity.status(HttpStatus.NO_CONTENT).body(o);
        } else {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(o);
        }
    }
}
