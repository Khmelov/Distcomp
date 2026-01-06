package com.labs.web.controller.v1;

import com.labs.service.dto.LabelDto;
import com.labs.service.dto.MessageDto;
import com.labs.service.dto.TweetDto;
import com.labs.service.dto.WriterDto;
import com.labs.service.service.TweetService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/tweets")
@RequiredArgsConstructor
public class TweetController {
    private final TweetService tweetService;

    @PostMapping
    public ResponseEntity<TweetDto> create(@Valid @RequestBody TweetDto tweetDto) {
        TweetDto created = tweetService.create(tweetDto);
        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }

    @GetMapping
    public ResponseEntity<List<TweetDto>> findAll(
            @RequestParam(required = false) List<String> labelNames,
            @RequestParam(required = false) List<Long> labelIds,
            @RequestParam(required = false) String writerLogin,
            @RequestParam(required = false) String title,
            @RequestParam(required = false) String content
    ) {
        boolean hasFilters = (labelNames != null && !labelNames.isEmpty()) ||
                            (labelIds != null && !labelIds.isEmpty()) ||
                            (writerLogin != null && !writerLogin.trim().isEmpty()) ||
                            (title != null && !title.trim().isEmpty()) ||
                            (content != null && !content.trim().isEmpty());
        
        List<TweetDto> tweets = hasFilters 
                ? tweetService.findByFilters(labelNames, labelIds, writerLogin, title, content)
                : tweetService.findAll();
        return ResponseEntity.ok(tweets);
    }

    @GetMapping("/{id}")
    public ResponseEntity<TweetDto> findById(@PathVariable Long id) {
        TweetDto tweet = tweetService.findById(id);
        return ResponseEntity.ok(tweet);
    }

    @PutMapping("/{id}")
    public ResponseEntity<TweetDto> update(@PathVariable Long id, @Valid @RequestBody TweetDto tweetDto) {
        TweetDto updated = tweetService.update(id, tweetDto);
        return ResponseEntity.ok(updated);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        tweetService.delete(id);
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }

    @GetMapping("/{id}/writer")
    public ResponseEntity<WriterDto> getWriterByTweetId(@PathVariable Long id) {
        WriterDto writer = tweetService.findWriterByTweetId(id);
        return ResponseEntity.ok(writer);
    }

    @GetMapping("/{id}/labels")
    public ResponseEntity<List<LabelDto>> getLabelsByTweetId(@PathVariable Long id) {
        List<LabelDto> labels = tweetService.findLabelsByTweetId(id);
        return ResponseEntity.ok(labels);
    }

    @GetMapping("/{id}/messages")
    public ResponseEntity<List<MessageDto>> getMessagesByTweetId(@PathVariable Long id) {
        List<MessageDto> messages = tweetService.findMessagesByTweetId(id);
        return ResponseEntity.ok(messages);
    }
}

