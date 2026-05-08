package by.liza.app.kafka;

import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;
import java.util.Map;
import java.util.concurrent.*;

@Slf4j @Component
public class NoteKafkaConsumer {
    private final Map<String, BlockingQueue<NoteKafkaMessage>> pending = new ConcurrentHashMap<>();

    public BlockingQueue<NoteKafkaMessage> register(String requestId) {
        BlockingQueue<NoteKafkaMessage> q = new LinkedBlockingQueue<>();
        pending.put(requestId, q);
        return q;
    }

    public void unregister(String requestId) { pending.remove(requestId); }

    @KafkaListener(topics = "${kafka.topic.out}", groupId = "${spring.kafka.consumer.group-id}")
    public void consume(NoteKafkaMessage message) {
        String reqId = message.getRequestId();
        if (reqId != null) {
            BlockingQueue<NoteKafkaMessage> q = pending.get(reqId);
            if (q != null) q.offer(message);
        }
    }
}