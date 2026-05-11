package by.bsuir.discussion.controllers.v2;

import by.bsuir.discussion.dto.requests.CommentRequestDto;
import by.bsuir.discussion.dto.responses.CommentResponseDto;
import by.bsuir.discussion.exceptions.Comments;
import by.bsuir.discussion.exceptions.EntityExistsException;
import by.bsuir.discussion.exceptions.NoEntityExistsException;
import by.bsuir.discussion.services.CommentService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
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
@RequestMapping("/api/v2.0/comments")
@RequiredArgsConstructor
public class CommentControllerV2 {

    private final CommentService commentService;

    @PostMapping
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<CommentResponseDto> create(@RequestBody CommentRequestDto comment) throws EntityExistsException {
        return ResponseEntity.status(HttpStatus.CREATED).body(commentService.create(comment));
    }

    @GetMapping("/{id}")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<CommentResponseDto> read(@PathVariable("id") Long id) throws NoEntityExistsException {
        return ResponseEntity.status(HttpStatus.OK).body(commentService.read(id).orElseThrow(() ->
                new NoEntityExistsException(Comments.NoEntityExistsException)));
    }

    @GetMapping
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<List<CommentResponseDto>> read() {
        return ResponseEntity.status(HttpStatus.OK).body(commentService.readAll());
    }

    @PutMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<CommentResponseDto> update(@RequestBody CommentRequestDto comment) throws NoEntityExistsException {
        return ResponseEntity.status(HttpStatus.OK).body(commentService.update(comment));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Long> delete(@PathVariable("id") Long id) throws NoEntityExistsException {
        return ResponseEntity.status(HttpStatus.NO_CONTENT).body(commentService.delete(id));
    }
}
