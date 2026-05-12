package com.bsuir.distcomp.service;

import com.bsuir.distcomp.dto.CommentListResponseTo;
import com.bsuir.distcomp.dto.CommentRequestTo;
import com.bsuir.distcomp.dto.CommentResponseTo;
import com.bsuir.distcomp.entity.Comment;
import com.bsuir.distcomp.entity.CommentKey;
import com.bsuir.distcomp.exception.EntityNotFoundException;
import com.bsuir.distcomp.kafka.CommentResponseProducer;
import com.bsuir.distcomp.mapper.CommentMapper;
import com.bsuir.distcomp.repository.CommentRepository;
import com.bsuir.types.Status;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.concurrent.atomic.AtomicLong;

@Slf4j
@Service
@RequiredArgsConstructor
public class CommentService {

    private final CommentRepository repository;
    private final CommentMapper mapper;
    private final CommentResponseProducer producer;
    private final AtomicLong idGenerator = new AtomicLong(System.currentTimeMillis());

    public void process(CommentRequestTo request) {
        log.info("🟠 [STEP 4] Processing: operation={}, correlationId={}",
                request.getOperation(), request.getCorrelationId());

        if (request.getOperation() == null) {
            log.error("🔴 [STEP 4] Operation is NULL! CorrelationId: {}", request.getCorrelationId());
            return;
        }

        switch (request.getOperation()) {
            case CREATE -> {
                log.info("🟠 [STEP 4] Routing to CREATE");
                create(request);
            }
            case UPDATE -> {
                log.info("🟠 [STEP 4] Routing to UPDATE");
                handleUpdate(request);
            }
            case DELETE -> {
                log.info("🟠 [STEP 4] Routing to DELETE");
                handleDelete(request);
            }
            case GET_ALL -> {
                log.info("🟠 [STEP 4] Routing to GET_ALL");
                handleGetAll(request);
            }
            case GET_BY_ID -> {
                log.info("🟠 [STEP 4] Routing to GET_BY_ID");
                handleGetById(request);
            }
        }
    }

    private void handleGetAll(CommentRequestTo request) {
        log.error("🔥 GET_ALL topicId from request = {}", request.getTopicId());

        List<Comment> comments = repository.findAll();

        log.error("🔥🔥🔥 TOTAL FROM DB = {}", comments.size());

        for (Comment c : comments) {
            log.error("👉 DB RECORD: id={}, topicId={}",
                    c.getKey().getId(),
                    c.getKey().getTopicId());
        }

        List<CommentResponseTo> list = comments.stream()
                .map(c -> {
                    CommentResponseTo dto = mapper.toDto(c);

                    log.error("👉 DTO: id={}, topicId={}",
                            dto.getId(),
                            dto.getTopicId());

                    return dto;
                })
                .toList();

        CommentListResponseTo response = new CommentListResponseTo();
        response.setComments(list);
        response.setCorrelationId(request.getCorrelationId());

        producer.sendList(response);
    }



    private void handleGetById(CommentRequestTo request) {
        log.info("🟠 handleGetById - id={}, topicId={}", request.getId(), request.getTopicId());


        if (request.getTopicId() == null) {
            List<Comment> all = repository.findAll();
            Comment comment = all.stream()
                    .filter(c -> c.getKey().getId().equals(request.getId()))
                    .findFirst()
                    .orElseThrow(() -> new EntityNotFoundException("Comment not found with id=" + request.getId()));

            CommentResponseTo response = mapper.toDto(comment);
            response.setStatus(comment.getStatus());
            response.setCorrelationId(request.getCorrelationId());
            producer.send(response);
            return;
        }

        // Если topicId передан — ищем по составному ключу
        CommentKey key = new CommentKey();
        key.setTopicId(request.getTopicId());
        key.setId(request.getId());

        Comment comment = repository.findById(key)
                .orElseThrow(() -> new EntityNotFoundException("Comment not found"));

        CommentResponseTo response = mapper.toDto(comment);
        response.setStatus(comment.getStatus());
        response.setCorrelationId(request.getCorrelationId());
        producer.send(response);
    }

    private void create(CommentRequestTo request) {
        log.error("🔥🔥🔥 CREATE INPUT topicId = {}", request.getTopicId());

        if (request.getTopicId() == null) {
            log.error("💣💣💣 topicId IS NULL ON CREATE !!!");
        }

        Comment comment = mapper.toEntity(request);

        CommentKey key = new CommentKey();
        key.setTopicId(request.getTopicId());
        key.setId(idGenerator.incrementAndGet());

        comment.setKey(key);

        log.error("🔥 BEFORE SAVE: topicId = {}", comment.getKey().getTopicId());

        Comment saved = repository.save(comment);

        log.error("🔥 AFTER SAVE: topicId = {}", saved.getKey().getTopicId());

        sendResponse(saved, request.getCorrelationId());
    }


    private void handleUpdate(CommentRequestTo request) {
        CommentKey key = new CommentKey();
        key.setTopicId(request.getTopicId());
        key.setId(request.getId());

        Comment existing = repository.findById(key)
                .orElseThrow(() -> new EntityNotFoundException("Comment not found"));

        existing.setContent(request.getContent());


        Status state = moderate(request.getContent());
        existing.setStatus(state);

        Comment saved = repository.save(existing);

        sendResponse(saved, request.getCorrelationId());
    }


    private void handleDelete(CommentRequestTo request) {
        log.info("🟠 DELETE - id={}, topicId={}", request.getId(), request.getTopicId());


        if (request.getTopicId() == null) {
            log.warn("🟠 topicId is null, searching for comment by id...");


            List<Comment> all = repository.findAll();
            Comment commentToDelete = all.stream()
                    .filter(c -> c.getKey().getId().equals(request.getId()))
                    .findFirst()
                    .orElse(null);

            if (commentToDelete != null) {
                log.info("🟠 Found comment to delete: topicId={}, id={}",
                        commentToDelete.getKey().getTopicId(),
                        commentToDelete.getKey().getId());
                repository.delete(commentToDelete);
            } else {
                log.warn("🟠 Comment not found for deletion: id={}", request.getId());
            }
        } else {

            CommentKey key = new CommentKey();
            key.setTopicId(request.getTopicId());
            key.setId(request.getId());

            if (repository.existsById(key)) {
                repository.deleteById(key);
                log.info("🟠 Deleted comment: topicId={}, id={}", key.getTopicId(), key.getId());
            } else {
                log.warn("🟠 Comment not found: topicId={}, id={}", key.getTopicId(), key.getId());
            }
        }


        CommentResponseTo response = new CommentResponseTo();
        response.setId(request.getId());
        response.setTopicId(request.getTopicId());
        response.setStatus(Status.APPROVE);
        response.setCorrelationId(request.getCorrelationId());

        producer.send(response);
    }
    private Status moderate(String content) {
        if (content == null) return Status.DECLINE;


        if (content.toLowerCase().contains("spam") ||
                content.toLowerCase().contains("bad")) {
            return Status.DECLINE;
        }

        return Status.APPROVE;
    }

    private void sendResponse(Comment comment, String correlationId) {
        CommentResponseTo response = mapper.toDto(comment);
        response.setCorrelationId(correlationId);

        producer.send(response);
    }
}