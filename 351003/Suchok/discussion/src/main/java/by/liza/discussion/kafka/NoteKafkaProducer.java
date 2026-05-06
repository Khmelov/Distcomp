package by.liza.discussion.kafka;

import lombok.RequiredArgsConstructor;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;
import java.util.List;

@Component @RequiredArgsConstructor
public class NoteKafkaProducer {
    private final KafkaTemplate<String, NoteKafkaMessage> kafkaTemplate;

    @Value("${kafka.topic.out}") private String outTopic;

    public void sendNote(String requestId, Long articleId, NoteKafkaDto note) {
        NoteKafkaMessage msg = NoteKafkaMessage.builder()
                .requestId(requestId).articleId(articleId).note(note).build();
        kafkaTemplate.send(new ProducerRecord<>(outTopic, String.valueOf(articleId), msg));
    }

    public void sendNoteList(String requestId, List<NoteKafkaDto> list) {
        NoteKafkaMessage msg = NoteKafkaMessage.builder()
                .requestId(requestId).articleId(0L).noteList(list).build();
        kafkaTemplate.send(new ProducerRecord<>(outTopic, "0", msg));
    }

    public void sendError(String requestId, Long articleId, int code, String message) {
        NoteKafkaMessage msg = NoteKafkaMessage.builder()
                .requestId(requestId).articleId(articleId)
                .errorCode(code).errorMessage(message).build();
        kafkaTemplate.send(new ProducerRecord<>(outTopic, String.valueOf(articleId), msg));
    }
}