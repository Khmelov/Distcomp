package com.blog.config;

import org.apache.kafka.clients.admin.AdminClient;
import org.apache.kafka.clients.admin.AdminClientConfig;
import org.apache.kafka.clients.admin.NewTopic;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.annotation.PostConstruct;
import java.util.Collections;
import java.util.Properties;
import java.util.concurrent.ExecutionException;

@Configuration
public class KafkaTopicInitializer {

    private static final Logger logger = LoggerFactory.getLogger(KafkaTopicInitializer.class);

    @Value("${spring.kafka.bootstrap-servers}")
    private String bootstrapServers;

    @Value("${kafka.topic.in.name:InTopic}")
    private String inTopicName;

    @Value("${kafka.topic.out.name:OutTopic}")
    private String outTopicName;

    @PostConstruct
    public void createTopics() {
        Properties properties = new Properties();
        properties.put(AdminClientConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapServers);

        try (AdminClient adminClient = AdminClient.create(properties)) {

            // Проверяем существование топиков
            boolean inTopicExists = adminClient.listTopics().names().get()
                    .contains(inTopicName);
            boolean outTopicExists = adminClient.listTopics().names().get()
                    .contains(outTopicName);

            // Создаем топики если они не существуют
            if (!inTopicExists) {
                NewTopic inTopic = new NewTopic(inTopicName, 3, (short) 1);
                adminClient.createTopics(Collections.singleton(inTopic)).all().get();
                logger.info("Created topic: {}", inTopicName);
            }

            if (!outTopicExists) {
                NewTopic outTopic = new NewTopic(outTopicName, 3, (short) 1);
                adminClient.createTopics(Collections.singleton(outTopic)).all().get();
                logger.info("Created topic: {}", outTopicName);
            }

        } catch (ExecutionException | InterruptedException e) {
            logger.error("Failed to create Kafka topics: {}", e.getMessage(), e);
        }
    }
}

