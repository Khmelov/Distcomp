package com.example.task310.service;

import com.example.task310.dto.PostResponseTo;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

@Service
public class KafkaConsumerService {

    @KafkaListener(topics = "OutTopic", groupId = "distcomp-group")
    public void listenOutTopic(PostResponseTo post) {
        System.out.println("Publisher received moderation result for post ID: " + post.id() + " State: " + post.state());
    }
}