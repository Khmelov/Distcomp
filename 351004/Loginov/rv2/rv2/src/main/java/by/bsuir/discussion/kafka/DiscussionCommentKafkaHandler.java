package by.bsuir.discussion.kafka;

import by.bsuir.discussion.exception.DiscussionNotFoundException;
import by.bsuir.discussion.service.DiscussionCommentService;
import by.bsuir.distcomp.dto.CommentDto;
import by.bsuir.distcomp.kafka.CommentKafkaRequest;
import by.bsuir.distcomp.kafka.CommentKafkaResponse;
import java.util.List;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.PageRequest;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@Service
public class DiscussionCommentKafkaHandler {
    private final DiscussionCommentService service;
    private final KafkaTemplate<String, Object> kafkaTemplate;
    private final String outTopic;

    public DiscussionCommentKafkaHandler(
            DiscussionCommentService service,
            KafkaTemplate<String, Object> kafkaTemplate,
            @Value("${kafka.topics.out}") String outTopic) {
        this.service = service;
        this.kafkaTemplate = kafkaTemplate;
        this.outTopic = outTopic;
    }

    @KafkaListener(topics = "${kafka.topics.in}", groupId = "discussion-comments")
    public void onRequest(CommentKafkaRequest request) {
        CommentKafkaResponse response;
        try {
            response = switch (request.operation()) {
                case CREATE -> CommentKafkaResponse.ok(request.requestId(), service.create(request.comment()));
                case GET -> CommentKafkaResponse.ok(request.requestId(), service.get(request.id()));
                case FIND_ALL -> findAll(request);
                case UPDATE -> CommentKafkaResponse.ok(request.requestId(), service.update(request.id(), request.comment()));
                case DELETE -> {
                    service.delete(request.id());
                    yield CommentKafkaResponse.empty(request.requestId());
                }
            };
        } catch (DiscussionNotFoundException ex) {
            response = CommentKafkaResponse.error(request.requestId(), "40403", "Comment not found");
        } catch (Exception ex) {
            response = CommentKafkaResponse.error(request.requestId(), "40001", "Request validation failed");
        }
        kafkaTemplate.send(outTopic, key(request), response);
    }

    private CommentKafkaResponse findAll(CommentKafkaRequest request) {
        int size = request.size() <= 0 ? 50 : request.size();
        List<CommentDto> comments = service.findAll(PageRequest.of(Math.max(0, request.page()), size));
        return CommentKafkaResponse.ok(request.requestId(), comments);
    }

    private String key(CommentKafkaRequest request) {
        if (request.comment() != null && request.comment().issueId() != null) {
            return request.comment().issueId().toString();
        }
        return String.valueOf(request.id());
    }
}
