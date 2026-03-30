package com.distcomp.service.kafka;

import com.distcomp.config.kafka.KafkaTopicProperties;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.admin.AdminClient;
import org.apache.kafka.clients.admin.NewTopic;
import org.apache.kafka.clients.admin.TopicDescription;
import org.springframework.kafka.core.KafkaAdmin;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutionException;

@Slf4j
@Service
@RequiredArgsConstructor
public class KafkaTopicCreatorService {

    private final KafkaAdmin kafkaAdmin;
    private final KafkaTopicProperties kafkaTopicProperties;

    public void createAllTopics() {
        if (!kafkaTopicProperties.getTopics().isAutoCreate()) {
            log.info("Kafka topic auto-creation is disabled");
            return;
        }

        log.info("Starting Kafka topic creation...");

        try (final AdminClient adminClient = AdminClient.create(kafkaAdmin.getConfigurationProperties())) {

            final List<NewTopic> topicsToCreate = buildTopicsToCreate();

            if (topicsToCreate.isEmpty()) {
                log.info("No topics configured for creation");
                return;
            }

            final List<NewTopic> newTopics = filterExistingTopics(adminClient, topicsToCreate);

            if (newTopics.isEmpty()) {
                log.info("All configured topics already exist");
                return;
            }

            
            adminClient.createTopics(newTopics).all().get();

            log.info("Successfully created {} Kafka topics: {}",
                    newTopics.size(),
                    newTopics.stream().map(NewTopic::name).toList());

        } catch (final InterruptedException | ExecutionException e) {
            log.error("Failed to create Kafka topics", e);
            Thread.currentThread().interrupt();
            throw new RuntimeException("Failed to create Kafka topics", e);
        }
    }


    private List<NewTopic> buildTopicsToCreate() {
        final List<NewTopic> topics = new ArrayList<>();
        final KafkaTopicProperties.Topics topicConfig = kafkaTopicProperties.getTopics();

        for (final KafkaTopicProperties.TopicConfig config : topicConfig.getList()) {
            final NewTopic newTopic = getNewTopic(config, topicConfig);


            if (config.getConfigs() != null && !config.getConfigs().isEmpty()) {
                newTopic.configs(config.getConfigs());
            }

            topics.add(newTopic);
        }

        return topics;
    }

    private static NewTopic getNewTopic(final KafkaTopicProperties.TopicConfig config, final KafkaTopicProperties.Topics topicConfig) {
        final int partitions = config.getPartitions() != null
                ? config.getPartitions()
                : topicConfig.getDefaultConfig().getPartitions();

        final short replicationFactor = config.getReplicationFactor() != null
                ? config.getReplicationFactor().shortValue()
                : topicConfig.getDefaultConfig().getReplicationFactor();

        return new NewTopic(
                config.getName(),
                partitions,
                replicationFactor
        );
    }


    private List<NewTopic> filterExistingTopics(final AdminClient adminClient, final List<NewTopic> allTopics)
            throws InterruptedException, ExecutionException {

        final List<String> topicNames = allTopics.stream()
                .map(NewTopic::name)
                .toList();

        final Map<String, TopicDescription> existingTopics = adminClient
                .describeTopics(topicNames)
                .allTopicNames()
                .get();

        return allTopics.stream()
                .filter(topic -> !existingTopics.containsKey(topic.name()))
                .toList();
    }


    public void validateTopics() {
        try (final AdminClient adminClient = AdminClient.create(kafkaAdmin.getConfigurationProperties())) {

            final List<String> requiredTopics = kafkaTopicProperties.getTopics().getList()
                    .stream()
                    .map(KafkaTopicProperties.TopicConfig::getName)
                    .toList();

            final Map<String, TopicDescription> existingTopics = adminClient
                    .describeTopics(requiredTopics)
                    .allTopicNames()
                    .get();

            final List<String> missingTopics = requiredTopics.stream()
                    .filter(topic -> !existingTopics.containsKey(topic))
                    .toList();

            if (!missingTopics.isEmpty()) {
                log.warn("Missing Kafka topics: {}", missingTopics);
                throw new RuntimeException("Required Kafka topics are missing: " + missingTopics);
            }

            log.info("All {} required Kafka topics are present", requiredTopics.size());

        } catch (final InterruptedException | ExecutionException e) {
            log.error("Failed to validate Kafka topics", e);
            Thread.currentThread().interrupt();
            throw new RuntimeException("Failed to validate Kafka topics", e);
        }
    }
}
