package com.sergey.orsik.discussion.kafka;

import com.sergey.orsik.discussion.exception.EntityNotFoundException;
import com.sergey.orsik.discussion.service.CommentDiscussionService;
import com.sergey.orsik.dto.kafka.CommentTransportReply;
import com.sergey.orsik.dto.kafka.CommentTransportRequest;
import com.sergey.orsik.dto.request.CommentRequestTo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

@Component
public class CommentInboundKafkaListener {

    private static final Logger log = LoggerFactory.getLogger(CommentInboundKafkaListener.class);

    private final CommentDiscussionService commentDiscussionService;
    private final KafkaTemplate<String, CommentTransportReply> replyKafkaTemplate;
    private final String outTopic;

    public CommentInboundKafkaListener(
            CommentDiscussionService commentDiscussionService,
            KafkaTemplate<String, CommentTransportReply> replyKafkaTemplate,
            @Value("${kafka.topic.out:OutTopic}") String outTopic) {
        this.commentDiscussionService = commentDiscussionService;
        this.replyKafkaTemplate = replyKafkaTemplate;
        this.outTopic = outTopic;
    }

    @KafkaListener(
            topics = "${kafka.topic.in:InTopic}",
            containerFactory = "commentRequestKafkaListenerContainerFactory")
    public void onInbound(CommentTransportRequest request) {
        if (request == null || request.getOperation() == null) {
            log.warn("Ignoring null Kafka inbound message");
            return;
        }
        try {
            switch (request.getOperation()) {
                case CREATE_ASYNC -> handleCreateAsync(request);
                case GET_BY_ID -> handleGetById(request);
                case FIND_ALL -> handleFindAll(request);
                case UPDATE -> handleUpdate(request);
                case DELETE_BY_ID -> handleDeleteById(request);
            }
        } catch (EntityNotFoundException ex) {
            log.debug("Kafka inbound not found: {}", ex.getMessage());
            if (request.getCorrelationId() != null) {
                sendReply(new CommentTransportReply(
                        request.getCorrelationId(),
                        true,
                        ex.getMessage(),
                        ex.getEntityName(),
                        ex.getId(),
                        null,
                        null));
            }
        } catch (Exception ex) {
            log.error("Kafka inbound handling failed for {}", request.getOperation(), ex);
            if (request.getCorrelationId() != null) {
                sendReply(new CommentTransportReply(
                        request.getCorrelationId(),
                        true,
                        ex.getMessage(),
                        null,
                        null,
                        null,
                        null));
            }
        }
    }

    private void handleCreateAsync(CommentTransportRequest request) {
        CommentRequestTo body = request.getBody();
        if (body == null || body.getId() == null) {
            log.warn("CREATE_ASYNC missing body or id");
            return;
        }
        commentDiscussionService.createFromKafkaAssignedId(body.getId(), body);
    }

    private void handleGetById(CommentTransportRequest request) {
        requireCorrelation(request);
        Long id = request.getCommentId();
        if (id == null) {
            sendReply(errorReply(request.getCorrelationId(), "commentId is required"));
            return;
        }
        sendReply(okSingle(request.getCorrelationId(), commentDiscussionService.findById(id)));
    }

    private void handleFindAll(CommentTransportRequest request) {
        requireCorrelation(request);
        int page = request.getPage() != null ? request.getPage() : 0;
        int size = request.getSize() != null ? request.getSize() : 20;
        String sortBy = request.getSortBy() != null ? request.getSortBy() : "id";
        String sortDir = request.getSortDir() != null ? request.getSortDir() : "asc";
        sendReply(new CommentTransportReply(
                request.getCorrelationId(),
                false,
                null,
                null,
                null,
                null,
                commentDiscussionService.findAll(
                        page, size, sortBy, sortDir, request.getTweetId(), request.getContent())));
    }

    private void handleUpdate(CommentTransportRequest request) {
        requireCorrelation(request);
        CommentRequestTo body = request.getBody();
        Long id = request.getCommentId();
        if (id == null || body == null) {
            sendReply(errorReply(request.getCorrelationId(), "commentId and body are required"));
            return;
        }
        sendReply(okSingle(request.getCorrelationId(), commentDiscussionService.update(id, body)));
    }

    private void handleDeleteById(CommentTransportRequest request) {
        requireCorrelation(request);
        Long id = request.getCommentId();
        if (id == null) {
            sendReply(errorReply(request.getCorrelationId(), "commentId is required"));
            return;
        }
        commentDiscussionService.deleteById(id);
        sendReply(new CommentTransportReply(request.getCorrelationId(), false, null, null, null, null, null));
    }

    private static void requireCorrelation(CommentTransportRequest request) {
        if (request.getCorrelationId() == null) {
            throw new IllegalStateException("correlationId is required for operation " + request.getOperation());
        }
    }

    private CommentTransportReply okSingle(String correlationId, com.sergey.orsik.dto.response.CommentResponseTo comment) {
        return new CommentTransportReply(correlationId, false, null, null, null, comment, null);
    }

    private static CommentTransportReply errorReply(String correlationId, String message) {
        return new CommentTransportReply(correlationId, true, message, null, null, null, null);
    }

    private void sendReply(CommentTransportReply reply) {
        if (reply.getCorrelationId() == null) {
            return;
        }
        replyKafkaTemplate.send(outTopic, reply.getCorrelationId(), reply);
    }
}
