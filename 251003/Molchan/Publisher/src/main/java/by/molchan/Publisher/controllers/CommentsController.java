package by.molchan.Publisher.controllers;

import by.molchan.Publisher.DTOs.Requests.CommentRequestDTO;
import by.molchan.Publisher.DTOs.Responses.CommentResponseDTO;
import by.molchan.Publisher.utils.CommentValidator;
import by.molchan.Publisher.utils.exceptions.ValidationException;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestClient;

import java.util.List;

@RestController
@RequestMapping("/comments")
public class CommentsController {

    private final RestClient restClient;
    private final CommentValidator commentValidator;
    @Autowired
    public CommentsController(RestClient restClient, CommentValidator commentValidator) {
        this.restClient = restClient;
        this.commentValidator = commentValidator;
    }


    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public CommentResponseDTO createComment(@RequestBody @Valid CommentRequestDTO commentRequestDTO, BindingResult bindingResult) {
        if (!bindingResult.hasFieldErrors()){
            commentValidator.validate(commentRequestDTO, bindingResult);
        }
        if (bindingResult.hasErrors()) throw new ValidationException(bindingResult);

        return restClient.post()
                .uri("/comments")
                .body(commentRequestDTO)
                .retrieve()
                .body(CommentResponseDTO.class);
    }

    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    public List<CommentResponseDTO> getAllComments() {
        return restClient.get()
                .uri("/comments")
                .retrieve()
                .body(new ParameterizedTypeReference<List<CommentResponseDTO>>() {});
    }

    @GetMapping("/{id}")
    @ResponseStatus(HttpStatus.OK)
    public CommentResponseDTO getCommentById(@PathVariable Long id) {
        return restClient.get()
                .uri("/comments/{id}", id)
                .retrieve()
                .body(CommentResponseDTO.class);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteComment(@PathVariable long id) {
        restClient.delete()
                .uri("/comments/{id}", id)
                .retrieve()
                .toBodilessEntity();
    }

    @PutMapping
    @ResponseStatus(HttpStatus.OK)
    public CommentResponseDTO updateComment(@RequestBody @Valid CommentRequestDTO commentRequestDTO) {
        return restClient.put()
                .uri("/comments")
                .body(commentRequestDTO)
                .retrieve()
                .body(CommentResponseDTO.class);
    }
}
