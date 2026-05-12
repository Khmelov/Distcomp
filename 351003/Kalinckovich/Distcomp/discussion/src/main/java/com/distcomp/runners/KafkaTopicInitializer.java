package com.distcomp.runners;


import com.distcomp.service.kafka.KafkaTopicCreatorService;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;

@Slf4j
@Configuration
@RequiredArgsConstructor
@Order(1)
public class KafkaTopicInitializer implements ApplicationRunner {

    private final KafkaTopicCreatorService kafkaTopicCreatorService;

    @Override
    public void run(@NonNull final ApplicationArguments args) {
        log.info("Initializing Kafka topics...");

        try {
            kafkaTopicCreatorService.createAllTopics();
            kafkaTopicCreatorService.validateTopics();
            log.info("Kafka topic initialization completed successfully");
        } catch (final Exception e) {
            log.error("Kafka topic initialization failed", e);
            throw new RuntimeException("Kafka topic initialization failed", e);
        }
    }
}
