package com.example.task310.service;

import com.example.task310.dto.PostRequestTo;
import com.example.task310.dto.PostResponseTo;
import com.example.task310.entity.Post;
import com.example.task310.enums.PostState;
import com.example.task310.mapper.EntityMapper;
import com.example.task310.repository.PostRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class KafkaConsumerService {
    private final PostRepository repository;
    private final EntityMapper mapper;
    private final KafkaTemplate<String, PostResponseTo> kafkaTemplate;

    @KafkaListener(topics = "InTopic")
    public void handlePostRequest(PostRequestTo dto) {
        Post entity = mapper.toEntity(dto);
        if (entity.getCountry() == null) entity.setCountry("default");
        if (dto.getContent() != null && dto.getContent().toLowerCase().contains("плохо")) {
            entity.setState(PostState.DECLINE);
        } else {
            entity.setState(PostState.APPROVE);
        }
        repository.save(entity);
        kafkaTemplate.send("OutTopic", String.valueOf(entity.getIssueId()), mapper.toResponse(entity));
    }
}