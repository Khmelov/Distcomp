package org.ex.distributed_computing.kafka.post;

import lombok.RequiredArgsConstructor;
import org.ex.distributed_computing.dto.request.PostRequestDTO;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.KafkaHeaders;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class PostKafkaService {

  @Value("${spring.kafka.topics.post.in}")
  private String postInTopicName;

  @Value("${spring.kafka.topics.post.out}")
  private String postOutTopicName;

  private final KafkaTemplate<String, PostRequestDTO> kafkaTemplate;

  public void publish(PostRequestDTO payload, Long messageKey) {
    var message = MessageBuilder.withPayload(payload)
        .setHeader(KafkaHeaders.TOPIC, postInTopicName)
        .setHeader(KafkaHeaders.KEY, messageKey)
        .build();

    kafkaTemplate.send(message);
  }
}
