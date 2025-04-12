package by.ryabchikov.tweet_service.client;

import by.ryabchikov.tweet_service.dto.comment.CommentRequestTo;
import by.ryabchikov.tweet_service.dto.comment.CommentResponseTo;
import by.ryabchikov.tweet_service.dto.comment.CommentUpdateRequestTo;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.List;

@FeignClient(name = "comment-service", url = "${comment.service.url}")
public interface CommentClient {

    @GetMapping("/api/v1.0/comments")
    List<CommentResponseTo> readAll();

    @GetMapping("/api/v1.0/comments/{id}")
    CommentResponseTo readById(@PathVariable("id") @Valid @NotNull Long id);

    @PostMapping("/api/v1.0/comments")
    CommentResponseTo create(@Valid @RequestBody CommentRequestTo commentRequestTo);

    @PutMapping("/api/v1.0/comments")
    CommentResponseTo update(@Valid @RequestBody CommentUpdateRequestTo commentUpdateRequestTo);

    @DeleteMapping("/api/v1.0/comments/{id}")
    void deleteById(@PathVariable("id") @Valid @NotNull Long id);
}
