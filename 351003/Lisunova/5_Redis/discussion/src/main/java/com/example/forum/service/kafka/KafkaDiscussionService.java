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
import java.util.Optional;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Slf4j
@Service
@RequiredArgsConstructor
public class KafkaDiscussionService {

    private final PostRepository postRepository;
    private final Map<Long, PostResponseTo> testCache = new ConcurrentHashMap<>();
    @KafkaListener(topics = "InTopic", groupId = "discussion-group")
    public Message<PostResponseTo> listen(@Payload PostRequestTo request,
                                          @Header(KafkaHeaders.RECEIVED_KEY) String key,
                                          @Header(KafkaHeaders.CORRELATION_ID) byte[] correlationId) {

        Long postId = (key != null && key.matches("\\d+")) ? Long.valueOf(key) : null;
        boolean isFetch = "FETCH".equalsIgnoreCase(request.getContent());

        PostResponseTo result;
        if (isFetch) {
            log.info("Processing FETCH for postId: {}", postId);
            Optional<Post> postOpt = postRepository.findById(postId);

            result = new PostResponseTo();
            result.setId(postId);
        } else {
            Post postEntity = new Post();
            postEntity.setId(postId);
            postEntity.setTopicId(request.getTopicId());
            postEntity.setContent(request.getContent());
            postEntity.setState(PostState.APPROVE);

            postRepository.save(postEntity);

            PostResponseTo response = mapToResponse(postEntity);
            testCache.put(postId, response);
            log.info("Saved and cached postId: {}", postId);
            result = mapToResponse(postEntity);
        }

        log.info("Sending reply for correlationId: {}", new String(correlationId));

        return MessageBuilder.withPayload(result)
                .setHeader(KafkaHeaders.CORRELATION_ID, correlationId)
                .setHeader(KafkaHeaders.TOPIC, "OutTopic")
                .build();
    }

    private PostResponseTo mapToResponse(Post entity) {
        PostResponseTo res = new PostResponseTo();
        res.setId(entity.getId());
        res.setTopicId(entity.getTopicId());
        res.setContent(entity.getContent());
        res.setState(entity.getState());
        return res;
    }
}