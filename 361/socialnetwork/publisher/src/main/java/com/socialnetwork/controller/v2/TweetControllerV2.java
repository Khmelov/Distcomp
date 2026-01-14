package com.socialnetwork.controller.v2;

import com.socialnetwork.dto.request.TweetRequestTo;
import com.socialnetwork.dto.response.TweetResponseTo;
import com.socialnetwork.exception.ResourceNotFoundException;
import com.socialnetwork.exception.UnauthorizedException;
import com.socialnetwork.repository.TweetRepository;
import com.socialnetwork.security.SecurityUtil;
import com.socialnetwork.service.TweetService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v2.0/tweets")
public class TweetControllerV2 {

    @Autowired
    private TweetService tweetService;

    @Autowired
    private TweetRepository tweetRepository;

    @GetMapping
    public ResponseEntity<List<TweetResponseTo>> getAllTweets() {
        // ADMIN - полный доступ, CUSTOMER - только чтение
        List<TweetResponseTo> tweets = tweetService.getAll();
        return ResponseEntity.ok(tweets);
    }

    @GetMapping("/{id}")
    public ResponseEntity<TweetResponseTo> getTweetById(@PathVariable Long id) {
        // ADMIN - полный доступ, CUSTOMER - только чтение
        TweetResponseTo tweet = tweetService.getById(id);
        return ResponseEntity.ok(tweet);
    }

    @PostMapping
    public ResponseEntity<TweetResponseTo> createTweet(@Valid @RequestBody TweetRequestTo request) {
        // ADMIN - полный доступ, CUSTOMER - может создавать только от своего имени
        if (SecurityUtil.isCustomer()) {
            Long currentUserId = SecurityUtil.getCurrentUserId();
            if (!currentUserId.equals(request.getUserId())) {
                throw new UnauthorizedException("You can only create tweets for yourself");
            }
        }
        TweetResponseTo createdTweet = tweetService.create(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(createdTweet);
    }

    @PutMapping("/{id}")
    public ResponseEntity<TweetResponseTo> updateTweet(@PathVariable Long id,
                                                       @Valid @RequestBody TweetRequestTo request) {
        // ADMIN - полный доступ, CUSTOMER - только свои твиты
        if (SecurityUtil.isCustomer()) {
            com.socialnetwork.model.Tweet tweet = tweetRepository.findById(id)
                    .orElseThrow(() -> new ResourceNotFoundException("Tweet not found with id: " + id));
            Long tweetOwnerId = tweet.getUser().getId();
            Long currentUserId = SecurityUtil.getCurrentUserId();
            
            if (!tweetOwnerId.equals(currentUserId)) {
                throw new UnauthorizedException("You can only update your own tweets");
            }
            // Проверяем, что пользователь не пытается изменить автора
            if (!request.getUserId().equals(currentUserId)) {
                throw new UnauthorizedException("You cannot change the author of your tweet");
            }
        }
        TweetResponseTo updatedTweet = tweetService.update(id, request);
        return ResponseEntity.ok(updatedTweet);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteTweet(@PathVariable Long id) {
        // ADMIN - полный доступ, CUSTOMER - только свои твиты
        if (SecurityUtil.isCustomer()) {
            com.socialnetwork.model.Tweet tweet = tweetRepository.findById(id)
                    .orElseThrow(() -> new ResourceNotFoundException("Tweet not found with id: " + id));
            Long tweetOwnerId = tweet.getUser().getId();
            Long currentUserId = SecurityUtil.getCurrentUserId();
            
            if (!tweetOwnerId.equals(currentUserId)) {
                throw new UnauthorizedException("You can only delete your own tweets");
            }
        }
        tweetService.delete(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/page")
    public ResponseEntity<Page<TweetResponseTo>> getTweetsPage(
            @PageableDefault(size = 10, sort = "created", direction = Sort.Direction.DESC) Pageable pageable) {
        // ADMIN - полный доступ, CUSTOMER - только чтение
        Page<TweetResponseTo> tweets = tweetService.getAll(pageable);
        return ResponseEntity.ok(tweets);
    }

    @GetMapping("/user/{userId}")
    public ResponseEntity<List<TweetResponseTo>> getTweetsByUserId(@PathVariable Long userId) {
        // ADMIN - полный доступ, CUSTOMER - только чтение
        List<TweetResponseTo> tweets = tweetService.getByUserId(userId);
        return ResponseEntity.ok(tweets);
    }

    @GetMapping("/user/{userId}/page")
    public ResponseEntity<Page<TweetResponseTo>> getTweetsByUserIdPage(
            @PathVariable Long userId,
            @PageableDefault(size = 10, sort = "created", direction = Sort.Direction.DESC) Pageable pageable) {
        // ADMIN - полный доступ, CUSTOMER - только чтение
        Page<TweetResponseTo> tweets = tweetService.getByUserId(userId, pageable);
        return ResponseEntity.ok(tweets);
    }

    @GetMapping("/label/{labelId}")
    public ResponseEntity<List<TweetResponseTo>> getTweetsByLabelId(@PathVariable Long labelId) {
        // ADMIN - полный доступ, CUSTOMER - только чтение
        List<TweetResponseTo> tweets = tweetService.getByLabelId(labelId);
        return ResponseEntity.ok(tweets);
    }

    @GetMapping("/label/{labelId}/page")
    public ResponseEntity<Page<TweetResponseTo>> getTweetsByLabelIdPage(
            @PathVariable Long labelId,
            @PageableDefault(size = 10, sort = "created", direction = Sort.Direction.DESC) Pageable pageable) {
        // ADMIN - полный доступ, CUSTOMER - только чтение
        Page<TweetResponseTo> tweets = tweetService.getByLabelId(labelId, pageable);
        return ResponseEntity.ok(tweets);
    }
}

