package com.distcomp.publisher.post.web;

import com.distcomp.publisher.article.repo.ArticleRepository;
import com.distcomp.publisher.exception.ResourceNotFoundException;
import com.distcomp.publisher.post.client.KafkaPostClient;
import com.distcomp.publisher.post.dto.PostRequest;
import com.distcomp.publisher.post.dto.PostResponse;
import jakarta.validation.Valid;
import java.util.List;
import java.util.Collections;
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

@RestController
@RequestMapping("/api/v1.0/posts")
public class PostController {

    private final KafkaPostClient kafkaClient;
    private final ArticleRepository articleRepository;

    public PostController(KafkaPostClient kafkaClient, ArticleRepository articleRepository) {
        this.kafkaClient = kafkaClient;
        this.articleRepository = articleRepository;
    }

    @PostMapping
    public ResponseEntity<PostResponse> create(@Valid @RequestBody PostRequest request) {
        if (!articleRepository.existsById(request.getArticleId())) {
            throw new ResourceNotFoundException("Article with id=" + request.getArticleId() + " not found");
        }
        return ResponseEntity.status(HttpStatus.CREATED).body(kafkaClient.create(request));
    }

    @GetMapping
    public ResponseEntity<List<PostResponse>> listAll() {
        try {
            List<PostResponse> result = kafkaClient.listAll();
            return ResponseEntity.ok(result != null ? result : Collections.emptyList());
        } catch (Exception ex) {
            return ResponseEntity.ok(Collections.emptyList());
        }
    }

    @GetMapping("/{id}")
    public ResponseEntity<PostResponse> getById(@PathVariable Long id) {
        PostResponse response = kafkaClient.getById(id);
        if (response == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(response);
    }

    @PutMapping
    public ResponseEntity<PostResponse> update(@Valid @RequestBody PostRequest request) {
        if (request.getId() == null) {
            return ResponseEntity.notFound().build();
        }

        PostResponse response = kafkaClient.updateById(request.getId(), request);
        if (response == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(response);
    }

    @PutMapping("/{id}")
    public ResponseEntity<PostResponse> updateById(
            @PathVariable Long id,
            @Valid @RequestBody PostRequest request
    ) {
        PostResponse response = kafkaClient.updateById(id, request);
        if (response == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteById(@PathVariable Long id) {
        boolean deleted = kafkaClient.deleteById(id);
        if (!deleted) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.noContent().build();
    }
}
