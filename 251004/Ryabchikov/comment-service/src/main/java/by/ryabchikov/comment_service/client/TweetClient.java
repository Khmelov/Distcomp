package by.ryabchikov.comment_service.client;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient(name = "tweet-service", url = "${tweet.service.url}")
public interface TweetClient {

//    @GetMapping("/api/v1.0/comments")
//    List<CommentResponseTo> readAll();

    @GetMapping("/api/v1.0/tweets/{id}")
    TweetResponseTo readById(@PathVariable("id") @Valid @NotNull Long id);

//    @PostMapping("/api/v1.0/comments")
//    CommentResponseTo create(@Valid @RequestBody CommentRequestTo commentRequestTo);
//
//    @PutMapping("/api/v1.0/comments")
//    CommentResponseTo update(@Valid @RequestBody CommentUpdateRequestTo commentUpdateRequestTo);
//
//    @DeleteMapping("/api/v1.0/comments/{id}")
//    void deleteById(@PathVariable("id") @Valid @NotNull Long id);
}
