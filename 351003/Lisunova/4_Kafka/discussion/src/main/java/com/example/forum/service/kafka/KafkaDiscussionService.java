package com.example.forum.service.kafka;

import com.example.forum.dto.request.PostRequestTo;
import com.example.forum.dto.response.PostResponseTo;
import com.example.forum.entity.Post;
import com.example.forum.entity.PostState;
import com.example.forum.repository.PostRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.stereotype.Service;
import org.springframework.kafka.support.KafkaHeaders;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.messaging.Message;
import org.springframework.messaging.support.MessageBuilder;
@Slf4j
@Service
@RequiredArgsConstructor
public class KafkaDiscussionService {

    private final PostRepository postRepository;
    private final KafkaTemplate<String, Object> kafkaTemplate;

    @KafkaListener(topics = "InTopic", groupId = "discussion-group")
    public PostResponseTo listen(@Payload PostRequestTo request,
                                 @Header(KafkaHeaders.CORRELATION_ID) byte[] correlationId,
                                 ConsumerRecord<String, PostRequestTo> record) {

        String messageKey = record.key();

        PostState resultState = request.getContent().toLowerCase().contains("badword")
                ? PostState.DECLINE
                : PostState.APPROVE;

        Post postEntity = new Post();
        postEntity.setTopicId(request.getTopicId());
        if (messageKey != null) postEntity.setId(Long.valueOf(messageKey));
        postEntity.setContent(request.getContent());
        postEntity.setState(resultState);

        try {
            postRepository.save(postEntity);
        } catch (Exception e) {
            log.error("Ошибка базы: {}", e.getMessage());
        }

        PostResponseTo response = new PostResponseTo();
        response.setId(postEntity.getId());
        response.setTopicId(postEntity.getTopicId());
        response.setContent(postEntity.getContent());
        response.setState(postEntity.getState());

        return response;
    }

    private Long generateIdFromKey(String key) {
        return System.currentTimeMillis();
    }
}