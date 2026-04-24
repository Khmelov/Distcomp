package com.distcomp.healthIndicator;

import com.distcomp.service.kafka.KafkaTopicCreatorService;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.health.contributor.Health;
import org.springframework.boot.health.contributor.HealthIndicator;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class KafkaTopicHealthIndicator implements HealthIndicator {

    private final KafkaTopicCreatorService kafkaTopicCreatorService;

    @Override
    public Health health() {
        try {
            kafkaTopicCreatorService.validateTopics();
            return Health.up().withDetail("kafka", "topics validated").build();
        } catch (final Exception e) {
            return Health.down(e)
                    .withDetail("kafka", "topic validation failed")
                    .build();
        }
    }
}