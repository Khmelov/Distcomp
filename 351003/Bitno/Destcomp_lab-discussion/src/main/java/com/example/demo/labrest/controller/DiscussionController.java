package com.example.demo.labrest.controller;

import com.example.demo.labrest.dto.KafkaNoticeRequest;
import com.example.demo.labrest.dto.KafkaNoticeResponse;
import com.example.demo.labrest.service.DiscussionService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1.0")
@RequiredArgsConstructor
@Slf4j
public class DiscussionController {

    private final DiscussionService service;

    @PutMapping("/notices/{id}")
    public ResponseEntity<KafkaNoticeResponse> updateNotice(
            @PathVariable Long id,
            @RequestBody KafkaNoticeResponse updateRequest) {

        log.info("REST API: Updating notice id={}", id);

        var kafkaRequest = KafkaNoticeRequest.builder()
                .operation(KafkaNoticeRequest.Operation.UPDATE)
                .id(id)
                .topicId(updateRequest.getTopicId())
                .content(updateRequest.getContent())
                .build();

        KafkaNoticeResponse entity = service.processNotice(kafkaRequest);

        return ResponseEntity.ok(KafkaNoticeResponse.builder()
                .id(entity.getId())
                .topicId(entity.getTopicId())
                .content(entity.getContent())
                .state(KafkaNoticeResponse.State.SUCCESS)
                .build());
    }

    @PostMapping("/notices")
    public ResponseEntity<KafkaNoticeResponse> createNotice(@RequestBody KafkaNoticeRequest request) {
        log.info("REST API: Creating notice for topic {}", request.getTopicId());
        KafkaNoticeResponse response = service.processNotice(request);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/notices/{id}")
    public ResponseEntity<KafkaNoticeResponse> getNotice(@PathVariable Long id) {
        log.info("REST API: Getting notice by id {}", id);
        return service.getNoticeAsResponse(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body(KafkaNoticeResponse.builder()
                                .state(KafkaNoticeResponse.State.NOT_FOUND)
                                .reason("Notice not found")
                                .build()));
    }

    @GetMapping("/notices")
    public ResponseEntity<List<KafkaNoticeResponse>> getNotices() {
        log.info("REST API: Getting all notices");
        List<KafkaNoticeResponse> responses = service.getAllNoticesAsResponses();
        return ResponseEntity.ok(responses);
    }

    @GetMapping("/topics/{topicId}/notices")
    public ResponseEntity<List<KafkaNoticeResponse>> getNoticesByTopic(@PathVariable Long topicId) {
        log.info("REST API: Getting notices for topic {}", topicId);
        List<KafkaNoticeResponse> responses = service.getNoticesByTopicAsResponses(topicId);
        return ResponseEntity.ok(responses);
    }

    @DeleteMapping("/notices/{id}")
    public ResponseEntity<Void> deleteNotice(@PathVariable Long id) {
        log.info("REST API: Deleting notice {}", id);
        service.deleteNotice(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/health")
    public ResponseEntity<String> health() {
        return ResponseEntity.ok("Discussion module is running");
    }
}