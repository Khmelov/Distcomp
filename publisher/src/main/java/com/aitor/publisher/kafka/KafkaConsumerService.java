package com.aitor.publisher.kafka;

import com.aitor.publisher.dto.MessageResponseTo;
import lombok.RequiredArgsConstructor;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeoutException;

@Service
@RequiredArgsConstructor
public class KafkaConsumerService {
    private static final long TIMEOUT = 30000;
    ConcurrentHashMap<String, List<MessageResponseTo>> resultsMap = new ConcurrentHashMap<>();

    @KafkaListener(topics = "OutTopic")
    public void consume(ConsumerRecord<String, List<MessageResponseTo>> record) {
        resultsMap.put(record.key(), record.value());
    }

    public List<MessageResponseTo> waitForResponse(String key) throws TimeoutException {
        long startTime = System.currentTimeMillis();

        while (System.currentTimeMillis() - startTime < TIMEOUT) {
            var result = resultsMap.remove(key);
            if (result != null) {
                return result;
            }
        }
        throw new TimeoutException("Response timeout exceeded");
    }
}
