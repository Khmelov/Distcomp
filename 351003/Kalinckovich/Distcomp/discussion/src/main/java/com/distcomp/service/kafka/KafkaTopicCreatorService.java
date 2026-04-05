package com.distcomp.service.kafka;

import com.distcomp.config.kafka.KafkaTopicProperties;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.admin.AdminClient;
import org.apache.kafka.clients.admin.ListTopicsOptions;
import org.apache.kafka.clients.admin.NewTopic;
import org.springframework.kafka.core.KafkaAdmin;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ExecutionException;
import java.util.stream.Collectors;

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

            final Set<String> existingTopics = listExistingTopics(adminClient);
            log.info("Existing Kafka topics: {}", existingTopics);

            final List<NewTopic> newTopics = topicsToCreate.stream()
                    .filter(topic -> !existingTopics.contains(topic.name()))
                    .collect(Collectors.toList());

            if (newTopics.isEmpty()) {
                log.info("All configured topics already exist");
                return;
            }

            adminClient.createTopics(newTopics).all().get();

            log.info("Successfully created {} Kafka topics: {}",
                    newTopics.size(),
                    newTopics.stream().map(NewTopic::name).collect(Collectors.toList()));

        } catch (final InterruptedException | ExecutionException e) {
            log.error("Failed to create Kafka topics", e);
            Thread.currentThread().interrupt();
            throw new RuntimeException("Failed to create Kafka topics", e);
        }
    }

    private Set<String> listExistingTopics(final AdminClient adminClient)
            throws InterruptedException, ExecutionException {

        final ListTopicsOptions options = new ListTopicsOptions().listInternal(false);

        return adminClient.listTopics(options)
                .names()
                .get();
    }

    private List<NewTopic> buildTopicsToCreate() {
        final List<NewTopic> topics = new ArrayList<>();
        final KafkaTopicProperties.Topics topicConfig = kafkaTopicProperties.getTopics();

        
        if (topicConfig.getMapping() == null || topicConfig.getMapping().isEmpty()) {
            log.warn("No topic mappings configured in kafka.topics.mapping");
            return topics;
        }

        for (final KafkaTopicProperties.TopicConfig config : topicConfig.getMapping().values()) {
            if (config == null || config.getName() == null) {
                continue;
            }

            final NewTopic newTopic = getNewTopic(config, topicConfig);

            final Map<String, String> configs = config.getConfigs();
            if (configs != null && !configs.isEmpty()) {
                newTopic.configs(configs);
            }

            topics.add(newTopic);
        }

        return topics;
    }

    private static NewTopic getNewTopic(final KafkaTopicProperties.TopicConfig config,
                                        final KafkaTopicProperties.Topics topicConfig) {
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

    public void validateTopics() {
        try (final AdminClient adminClient = AdminClient.create(kafkaAdmin.getConfigurationProperties())) {

            final KafkaTopicProperties.Topics topicConfig = kafkaTopicProperties.getTopics();

            
            if (topicConfig.getMapping() == null || topicConfig.getMapping().isEmpty()) {
                log.warn("No topic mappings to validate");
                return;
            }

            final List<String> requiredTopics = topicConfig.getMapping().values()
                    .stream()
                    .filter(config -> config != null && config.getName() != null)
                    .map(KafkaTopicProperties.TopicConfig::getName)
                    .toList();

            if (requiredTopics.isEmpty()) {
                log.warn("No topics to validate");
                return;
            }

            final Set<String> existingTopics = listExistingTopics(adminClient);

            final List<String> missingTopics = requiredTopics.stream()
                    .filter(topic -> !existingTopics.contains(topic))
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