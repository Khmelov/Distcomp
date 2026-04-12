package org.polozkov.config;


import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.polozkov.other.record.CommentResponseRecord;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.config.ConcurrentKafkaListenerContainerFactory;
import org.springframework.kafka.core.ConsumerFactory;
import org.springframework.kafka.core.DefaultKafkaConsumerFactory;
import org.springframework.kafka.support.serializer.JsonDeserializer;

import java.util.HashMap;
import java.util.Map;
@Configuration
public class KafkaConsumerConfig {

    @Value("${spring.kafka.bootstrap-servers}")
    private String bootstrapServers;

    private Map<String, Object> getConfig(Object recordClass) {
        Map<String, Object> props = new HashMap<>();

        // 1. Базовое подключение
        props.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapServers);
        props.put(ConsumerConfig.GROUP_ID_CONFIG, "publisher-group");

        // 2. Десериализаторы (КЛАССЫ)
        props.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class);
        props.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, JsonDeserializer.class);

        // 3. Настройки JSON (строго через свойства, чтобы избежать deprecated конструкторов)

        // Указываем в какой класс превращать JSON (полное имя класса)
        props.put(JsonDeserializer.VALUE_DEFAULT_TYPE, recordClass);

        // Доверяем всем пакетам при десериализации
        props.put(JsonDeserializer.TRUSTED_PACKAGES, "*");

        // Игнорируем заголовки типов от отправителя (важно, если в сервисах разные имена пакетов)
        props.put(JsonDeserializer.USE_TYPE_INFO_HEADERS, false);
        return props;
    }

    @Bean
    public ConcurrentKafkaListenerContainerFactory<String, CommentResponseRecord> kafkaListenerContainerFactory() {
        ConcurrentKafkaListenerContainerFactory<String, CommentResponseRecord> factory =
                new ConcurrentKafkaListenerContainerFactory<>();
        factory.setConsumerFactory(new DefaultKafkaConsumerFactory<>(getConfig(CommentResponseRecord.class)));

        return factory;
    }
}
