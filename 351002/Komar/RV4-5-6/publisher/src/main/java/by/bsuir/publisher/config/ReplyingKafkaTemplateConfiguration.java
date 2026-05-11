package by.bsuir.publisher.config;

import by.bsuir.publisher.dto.CommentActionDto;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.common.serialization.StringSerializer;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.core.ConsumerFactory;
import org.springframework.kafka.core.DefaultKafkaProducerFactory;
import org.springframework.kafka.core.ProducerFactory;
import org.springframework.kafka.listener.ContainerProperties;
import org.springframework.kafka.listener.KafkaMessageListenerContainer;
import org.springframework.kafka.requestreply.ReplyingKafkaTemplate;
import org.springframework.kafka.support.serializer.JsonSerializer;

import java.util.HashMap;
import java.util.Map;

@Configuration
public class ReplyingKafkaTemplateConfiguration {

    @Value("${topic.outTopic}")
    private String replyTopic;

    @Value("${spring.kafka.producer.bootstrap-servers}")
    private String bootstrapServers;

    @Bean
    public ReplyingKafkaTemplate<String, CommentActionDto, CommentActionDto>
    replyKafkaTemplate(ProducerFactory<String, CommentActionDto> pf,
                       KafkaMessageListenerContainer<String, CommentActionDto> container) {
        return new ReplyingKafkaTemplate<>(pf, container);
    }

    // Listener Container to be set up in ReplyingKafkaTemplate
    @Bean
    public KafkaMessageListenerContainer<String, CommentActionDto> replyContainer(ConsumerFactory<String, CommentActionDto> cf) {
        ContainerProperties containerProperties = new ContainerProperties(replyTopic);
        return new KafkaMessageListenerContainer<>(cf, containerProperties);
    }

    // Default Producer Factory to be used in ReplyingKafkaTemplate
    @Bean
    public ProducerFactory<String, CommentActionDto> producerFactory() {
        return new DefaultKafkaProducerFactory<>(producerConfigs());
    }

    // Standard KafkaProducer settings - specifying broker and serializer
    @Bean
    public Map<String, Object> producerConfigs() {
        Map<String, Object> props = new HashMap<>();
        props.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG,
                bootstrapServers);
        props.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG,
                StringSerializer.class);
        props.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, JsonSerializer.class);
        props.put(JsonSerializer.TYPE_MAPPINGS, "CommentActionDto:by.bsuir.publisher.dto.CommentActionDto");
        return props;
    }
}
