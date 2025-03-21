package org.ex.distributed_computing.controller;

import jakarta.validation.Valid;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.ex.distributed_computing.dto.request.NewsRequestDTO;
import org.ex.distributed_computing.dto.response.NewsResponseDTO;
import org.ex.distributed_computing.service.NewsService;
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
@RequestMapping("/news")
@RequiredArgsConstructor
public class NewsController {

  private final NewsService newsService;

  @GetMapping
  public List<NewsResponseDTO> getAllNews() {
    return newsService.getAllNews();
  }

  @GetMapping("/{id}")
  public ResponseEntity<NewsResponseDTO> getNewsById(@PathVariable Long id) {
    return ResponseEntity.ok(newsService.getNewsById(id));
  }

  @PostMapping
  public ResponseEntity<NewsResponseDTO> createNews(@Valid @RequestBody NewsRequestDTO requestDTO) {
    return ResponseEntity.status(HttpStatus.CREATED).body(newsService.createNews(requestDTO));
  }

  @PutMapping
  public ResponseEntity<NewsResponseDTO> updateNews(@Valid @RequestBody NewsRequestDTO requestDTO) {
    return ResponseEntity.ok(newsService.updateNews(requestDTO));
  }

  @DeleteMapping("/{id}")
  public ResponseEntity<Void> deleteNews(@PathVariable Long id) {
    newsService.deleteNews(id);
    return ResponseEntity.noContent().build();
  }
}

