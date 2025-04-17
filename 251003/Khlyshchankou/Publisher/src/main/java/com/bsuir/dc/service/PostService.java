package com.bsuir.dc.service;

import com.bsuir.dc.dto.kafka.InTopicDTO;
import com.bsuir.dc.dto.kafka.OutTopicDTO;
import com.bsuir.dc.dto.request.PostRequestTo;
import com.bsuir.dc.dto.response.PostResponseTo;
import com.bsuir.dc.util.exception.EntityNotFoundException;
import com.bsuir.dc.util.mapper.PostMapper;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.common.KafkaException;
import org.apache.kafka.common.header.internals.RecordHeader;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpMethod;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.requestreply.KafkaReplyTimeoutException;
import org.springframework.kafka.requestreply.ReplyingKafkaTemplate;
import org.springframework.kafka.requestreply.RequestReplyFuture;
import org.springframework.kafka.support.KafkaHeaders;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

@Service
public class PostService {
    private final ReplyingKafkaTemplate<String, InTopicDTO, OutTopicDTO> replyingKafkaTemplate;
    private final KafkaTemplate<String, InTopicDTO> kafkaTemplate;
    private final PostMapper postMapper;
    private static final String IN_TOPIC = "InTopic";
    private static final String OUT_TOPIC = "OutTopic";

    @Autowired
    public PostService(
            ReplyingKafkaTemplate<String, InTopicDTO, OutTopicDTO> replyingKafkaTemplate,
            KafkaTemplate<String, InTopicDTO> kafkaTemplate,
            PostMapper postMapper
    ) {
        this.replyingKafkaTemplate = replyingKafkaTemplate;
        this.kafkaTemplate = kafkaTemplate;
        this.postMapper = postMapper;
    }

    public PostResponseTo createPost(PostRequestTo requestDTO) {
        Long generatedId = UUID.randomUUID().getMostSignificantBits() & Long.MAX_VALUE;
        requestDTO.setId(generatedId);

        InTopicDTO inDto = new InTopicDTO(
                "POST",
                requestDTO,
                "PENDING"
        );

        kafkaTemplate.send(IN_TOPIC, generatedId.toString(), inDto);

        return postMapper.toPostResponse(requestDTO);
    }

    public List<PostResponseTo> getAllPosts() {
        InTopicDTO request = new InTopicDTO(
                "GET",
                null,
                "PENDING"
        );
        String id = UUID.randomUUID().toString();
        OutTopicDTO response = sendAndReceiveInternal(request, id);
        return response.getPostResponsesListDTO();
    }

    public PostResponseTo getPostById(Long id) {
        InTopicDTO request = new InTopicDTO(
                "GET",
                new PostRequestTo(id),
                "PENDING"
        );
        OutTopicDTO response = sendAndReceiveInternal(request, id.toString());
        if (response.getError() != null && !response.getError().isEmpty()){
            throw new EntityNotFoundException(response.getError());
        }
        return response.getPostResponseDTO();
    }

    public PostResponseTo processPostRequest(String httpMethod, PostRequestTo postRequestDTO) {
        InTopicDTO request = new InTopicDTO(
                httpMethod,
                postRequestDTO,
                "PENDING"
        );

        OutTopicDTO response = sendAndReceiveInternal(request, postRequestDTO.getId().toString());
        if (response.getError() != null && !response.getError().contains("Not found")){
            throw new EntityNotFoundException(response.getError());
        }
        return response.getPostResponseDTO();

    }

    private OutTopicDTO sendAndReceiveInternal(InTopicDTO request, String correlationId) {
        ProducerRecord<String, InTopicDTO> record = new ProducerRecord<>(IN_TOPIC, correlationId, request);
        RequestReplyFuture<String, InTopicDTO, OutTopicDTO> future = replyingKafkaTemplate.sendAndReceive(record);
        try {
            ConsumerRecord<String, OutTopicDTO> response = future.get(10, TimeUnit.SECONDS);
            return response.value();
        } catch (TimeoutException e) {
            throw new KafkaReplyTimeoutException("Timeout waiting for post data");
        } catch (InterruptedException | ExecutionException e) {
            throw new KafkaException("Error processing Kafka request", e);
        }
    }
}
