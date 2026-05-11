package org.discussion.service.kafka;

import lombok.RequiredArgsConstructor;
import org.discussion.dto.comment.CommentResponseTo;
import org.discussion.other.enums.RequestMethod;
import org.discussion.other.record.CommentResponseRecord;
import org.discussion.other.record.CommentUploadRecord;
import org.discussion.service.comment.CommentService;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;

@Service
@RequiredArgsConstructor
public class KafkaDiscussionListener {

    private final CommentService commentService;
    private final KafkaTemplate<String, CommentResponseRecord> kafkaTemplate;

    @KafkaListener(topics = "in-topic", groupId = "discussion-group", containerFactory = "kafkaListenerContainerFactory")
    public void listen(CommentUploadRecord record) {
        if (record.method().equals(RequestMethod.PUT)) System.out.println("hhhhhhhhhhhh");
        CommentResponseRecord response;
        try {
            List<CommentResponseTo> result = switch (record.method()) {
                case GET -> {
                    if (record.data() != null && record.data().getId() != null) {
                        yield List.of(commentService.getComment(record.data().getId()));
                    } else {
                        yield commentService.getAllComments();
                    }
                }
                case POST -> {
                    yield List.of(commentService.createComment(record.data()));
                }
                case PUT -> {
                    yield  List.of(commentService.updateComment(record.data()));
                }
                case DELETE -> {
                    commentService.deleteComment(record.data().getId());
                    yield Collections.emptyList();
                }
            };

            response = new CommentResponseRecord(record.id(), result, null);
        } catch (Exception e) {
            response = new CommentResponseRecord(record.id(), null, e.getMessage());
        }

        kafkaTemplate.send("out-topic", record.id().toString(), response);
    }
}