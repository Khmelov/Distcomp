package com.distcomp.config.kafka;


import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;

@Data
@Component
@ConfigurationProperties(prefix = "kafka")
public class KafkaTopicProperties {

    private static final int DEFAULT_PARTITION_COUNT = 3;
    private static final short DEFAULT_REPLICATION_FACTOR = 1;

    private Topics topics = new Topics();

    @Data
    public static class Topics {
        private boolean autoCreate = true;
        private Default defaultConfig = new Default();
        private Map<String, TopicConfig> mapping;
    }

    @Data
    public static class Default {
        private int partitions = DEFAULT_PARTITION_COUNT;
        private short replicationFactor = DEFAULT_REPLICATION_FACTOR;
    }

    @Data
    public static class TopicConfig {
        private String name;
        private Integer partitions;
        private Integer replicationFactor;
        private Map<String, String> configs;
    }

    public String getTopicName(final KafkaTopic topic) {
        final Map<String, TopicConfig> topicsMapping = topics.getMapping();
        if (topicsMapping != null) {
            final String topicName = topic.name();
            if (topicsMapping.containsKey(topicName)) {
                return topicsMapping.get(topicName).getName();
            }
        }
        return topic.getName();
    }
}
