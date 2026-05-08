package by.liza.app.kafka;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

@Slf4j @Component @RequiredArgsConstructor
public class NoteKafkaProducer {
    private final KafkaTemplate<String, NoteKafkaMessage> kafkaTemplate;

    @Value("${kafka.topic.in}")
    private String inTopic;

    public void send(NoteKafkaMessage message) {
        String key = String.valueOf(message.getArticleId());
        kafkaTemplate.send(new ProducerRecord<>(inTopic, key, message));
    }
}