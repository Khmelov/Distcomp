package by.distcomp.app.kafka;

import by.distcomp.app.dto.NoteRequestTo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@Service
public class NoteKafkaProducer {

    @Autowired
    private KafkaTemplate<String, NoteRequestTo> kafkaTemplate;

    public void sendToModeration(NoteRequestTo note) {

        String key = String.valueOf(note.articleId());

        kafkaTemplate.send("InTopic", key, note);
        kafkaTemplate.flush();
    }
}