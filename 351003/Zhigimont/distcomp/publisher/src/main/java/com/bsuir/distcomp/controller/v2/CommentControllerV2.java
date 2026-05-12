package com.bsuir.distcomp.controller.v2;

import com.bsuir.distcomp.dto.CommentListResponseTo;
import com.bsuir.distcomp.dto.CommentRequestTo;
import com.bsuir.distcomp.dto.CommentResponseTo;
import com.bsuir.distcomp.kafka.CommentProducer;
import com.bsuir.distcomp.service.ResponseHolder;
import com.bsuir.types.OperationType;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;

@RestController
@RequestMapping("/api/v2.0/comments")
@RequiredArgsConstructor
public class CommentControllerV2 {

    private final CommentProducer producer;
    private final ResponseHolder holder;

    @GetMapping
    public ResponseEntity<?> getAll() throws Exception {
        String correlationId = UUID.randomUUID().toString();
        CommentRequestTo request = new CommentRequestTo();
        request.setOperation(OperationType.GET_ALL);
        request.setCorrelationId(correlationId);

        CompletableFuture<CommentListResponseTo> future = holder.createList(correlationId);
        producer.send(request);

        CommentListResponseTo response = future.get(10, TimeUnit.SECONDS);
        List<CommentResponseTo> comments = response.getComments();
        return ResponseEntity.ok(comments != null ? comments : List.of());
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getById(@PathVariable Long id) throws Exception {
        String correlationId = UUID.randomUUID().toString();
        CommentRequestTo request = new CommentRequestTo();
        request.setId(id);
        request.setOperation(OperationType.GET_BY_ID);
        request.setCorrelationId(correlationId);

        CompletableFuture<CommentResponseTo> future = holder.createSingle(correlationId);
        producer.send(request);

        return ResponseEntity.ok(future.get(10, TimeUnit.SECONDS));
    }
}