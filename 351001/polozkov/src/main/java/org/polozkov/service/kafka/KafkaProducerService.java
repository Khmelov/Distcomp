package org.polozkov.service.kafka;

import lombok.RequiredArgsConstructor;
import org.polozkov.other.record.CommentUploadRecord;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class KafkaProducerService {

    private final KafkaTemplate<String, CommentUploadRecord> kafkaTemplate;

    public void sendCommentRequest(CommentUploadRecord record) {
        kafkaTemplate.send("in-topic", record.id().toString(), record);
    }

}
