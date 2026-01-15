package com.example.task310rest.controller;

import com.example.task310rest.dto.request.TweetRequestTo;
import com.example.task310rest.dto.response.MarkResponseTo;
import com.example.task310rest.dto.response.NoteResponseTo;
import com.example.task310rest.dto.response.TweetResponseTo;
import com.example.task310rest.dto.response.UserResponseTo;
import com.example.task310rest.service.TweetService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * REST контроллер для работы с Tweet
 * Базовый путь: /api/v1.0/tweets
 */
@RestController
@RequestMapping("/api/v1.0/tweets")
@RequiredArgsConstructor
public class TweetController {
    
    private final TweetService tweetService;
    
    /**
     * Создать новый твит
     * POST /api/v1.0/tweets
     * @return 201 Created + TweetResponseTo
     */
    @PostMapping
    public ResponseEntity<TweetResponseTo> create(@Valid @RequestBody TweetRequestTo requestTo) {
        TweetResponseTo response = tweetService.create(requestTo);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }
    
    /**
     * Получить твит по ID
     * GET /api/v1.0/tweets/{id}
     * @return 200 OK + TweetResponseTo
     */
    @GetMapping("/{id}")
    public ResponseEntity<TweetResponseTo> getById(@PathVariable Long id) {
        TweetResponseTo response = tweetService.getById(id);
        return ResponseEntity.ok(response);
    }
    
    /**
     * Получить все твиты (с опциональными фильтрами)
     * GET /api/v1.0/tweets
     * Параметры: markIds, markName, login, title, content
     * @return 200 OK + List<TweetResponseTo>
     */
    @GetMapping
    public ResponseEntity<List<TweetResponseTo>> getAll(
            @RequestParam(required = false) List<Long> markIds,
            @RequestParam(required = false) String markName,
            @RequestParam(required = false) String login,
            @RequestParam(required = false) String title,
            @RequestParam(required = false) String content) {
        
        // Если есть фильтры, используем фильтрацию
        if (markIds != null || markName != null || login != null || title != null || content != null) {
            List<TweetResponseTo> response = tweetService.getTweetsWithFilters(
                    markIds, markName, login, title, content);
            return ResponseEntity.ok(response);
        }
        
        // Иначе возвращаем все твиты
        List<TweetResponseTo> response = tweetService.getAll();
        return ResponseEntity.ok(response);
    }
    
    /**
     * Обновить твит (полное обновление)
     * PUT /api/v1.0/tweets/{id}
     * @return 200 OK + TweetResponseTo
     */
    @PutMapping("/{id}")
    public ResponseEntity<TweetResponseTo> update(
            @PathVariable Long id,
            @Valid @RequestBody TweetRequestTo requestTo) {
        TweetResponseTo response = tweetService.update(id, requestTo);
        return ResponseEntity.ok(response);
    }
    
    /**
     * Частичное обновление твита
     * PATCH /api/v1.0/tweets/{id}
     * @return 200 OK + TweetResponseTo
     */
    @PatchMapping("/{id}")
    public ResponseEntity<TweetResponseTo> partialUpdate(
            @PathVariable Long id,
            @RequestBody TweetRequestTo requestTo) {
        TweetResponseTo response = tweetService.partialUpdate(id, requestTo);
        return ResponseEntity.ok(response);
    }
    
    /**
     * Удалить твит
     * DELETE /api/v1.0/tweets/{id}
     * @return 204 No Content
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        tweetService.delete(id);
        return ResponseEntity.noContent().build();
    }
    
    /**
     * Получить пользователя по ID твита
     * GET /api/v1.0/tweets/{id}/user
     * @return 200 OK + UserResponseTo
     */
    @GetMapping("/{id}/user")
    public ResponseEntity<UserResponseTo> getUserByTweetId(@PathVariable Long id) {
        UserResponseTo response = tweetService.getUserByTweetId(id);
        return ResponseEntity.ok(response);
    }
    
    /**
     * Получить метки по ID твита
     * GET /api/v1.0/tweets/{id}/marks
     * @return 200 OK + List<MarkResponseTo>
     */
    @GetMapping("/{id}/marks")
    public ResponseEntity<List<MarkResponseTo>> getMarksByTweetId(@PathVariable Long id) {
        List<MarkResponseTo> response = tweetService.getMarksByTweetId(id);
        return ResponseEntity.ok(response);
    }
    
    /**
     * Получить заметки по ID твита
     * GET /api/v1.0/tweets/{id}/notes
     * @return 200 OK + List<NoteResponseTo>
     */
    @GetMapping("/{id}/notes")
    public ResponseEntity<List<NoteResponseTo>> getNotesByTweetId(@PathVariable Long id) {
        List<NoteResponseTo> response = tweetService.getNotesByTweetId(id);
        return ResponseEntity.ok(response);
    }
}
