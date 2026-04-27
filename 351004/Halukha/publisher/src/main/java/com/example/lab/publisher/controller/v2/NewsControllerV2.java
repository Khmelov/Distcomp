package com.example.lab.publisher.controller.v2;

import java.util.List;

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

import com.example.lab.publisher.dto.NewsRequestTo;
import com.example.lab.publisher.dto.NewsResponseTo;
import com.example.lab.publisher.dto.UserResponseTo;
import com.example.lab.publisher.security.OwnershipService;
import com.example.lab.publisher.service.NewsService;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/v2.0/news")
public class NewsControllerV2 {

    private final NewsService newsService;
    private final OwnershipService ownership;

    public NewsControllerV2(NewsService newsService, OwnershipService ownership) {
        this.newsService = newsService;
        this.ownership = ownership;
    }

    @GetMapping
    public ResponseEntity<List<NewsResponseTo>> getAllNews() {
        return ResponseEntity.ok(newsService.getAllNews());
    }

    @GetMapping("/{id}")
    public ResponseEntity<NewsResponseTo> getNews(@PathVariable Long id) {
        return ResponseEntity.ok(newsService.getNewsById(id));
    }

    @PostMapping
    @PreAuthorize("hasRole('ADMIN') or hasRole('CUSTOMER')")
    public ResponseEntity<NewsResponseTo> createNews(@Valid @RequestBody NewsRequestTo news) {
        Long me = ownership.currentUserId();
        NewsRequestTo adjusted = news;
        if (me != null) {
            adjusted = new NewsRequestTo(me, news.getTitle(), news.getContent(), news.getCreated(), news.getModified(),
                    news.getMarkers());
        }
        return ResponseEntity.status(HttpStatus.CREATED).body(newsService.createNews(adjusted));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN') or @ownership.canModifyNews(#id)")
    public ResponseEntity<NewsResponseTo> updateNews(@PathVariable Long id, @Valid @RequestBody NewsRequestTo news) {
        Long me = ownership.currentUserId();
        NewsRequestTo adjusted = news;
        if (me != null) {
            adjusted = new NewsRequestTo(me, news.getTitle(), news.getContent(), news.getCreated(), news.getModified(),
                    news.getMarkers());
        }
        return ResponseEntity.ok(newsService.updateNews(id, adjusted));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN') or @ownership.canModifyNews(#id)")
    public ResponseEntity<Void> deleteNews(@PathVariable Long id) {
        newsService.deleteNews(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/user/{id}")
    public ResponseEntity<UserResponseTo> getUserByNewsId(@PathVariable Long id) {
        return ResponseEntity.ok(newsService.getUserByNewsId(id));
    }
}

