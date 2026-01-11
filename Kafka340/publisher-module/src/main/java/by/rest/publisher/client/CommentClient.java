package by.rest.publisher.client;

import by.rest.publisher.dto.comment.CommentResponseTo;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.List;

@FeignClient(name = "discussion-service", url = "http://localhost:24130")
public interface CommentClient {
    
    @GetMapping("/api/v1.0/comments/story/{storyId}")
    List<CommentResponseTo> getCommentsByStoryId(@PathVariable("storyId") Long storyId);
}