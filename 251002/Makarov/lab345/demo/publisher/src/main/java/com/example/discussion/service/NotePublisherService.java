package com.example.discussion.service;


import com.example.discussion.dto.NoteRequestTo;
import com.example.discussion.dto.NoteResponseTo;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.KafkaHeaders;
import org.springframework.messaging.Message;
import org.springframework.messaging.support.MessageBuilder;

import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

@Service
public class NotePublisherService {
    private final KafkaTemplate<String, Object> kafkaTemplate;
    private final Map<String, ResponseHolder<?>> pendingRequests = new ConcurrentHashMap<>();

    private static final String REQUEST_TOPIC = "InTopic";
    private static final String REPLY_TOPIC = "OutTopic";
    private static final long REPLY_TIMEOUT = 60L;

    public NotePublisherService(KafkaTemplate<String, Object> kafkaTemplate) {
        this.kafkaTemplate = kafkaTemplate;
    }

    @KafkaListener(
            topics = REPLY_TOPIC,
            containerFactory = "kafkaJsonListenerContainerFactory"
    )
    public void listen(ConsumerRecord<String, Object> record) {
        String correlationId = record.headers().lastHeader(KafkaHeaders.CORRELATION_ID) != null
                ? new String(record.headers().lastHeader(KafkaHeaders.CORRELATION_ID).value())
                : null;

        if (correlationId == null) return;

        ResponseHolder<?> holder = pendingRequests.get(correlationId);
        if (holder != null) {
            holder.setResponse(record.value());
            holder.latch.countDown();
        }
    }

    private <T> T sendAndReceive(NoteRequestTo request, String operation, Class<T> responseType)
            throws TimeoutException {

        String correlationId = UUID.randomUUID().toString();
        ResponseHolder<T> holder = new ResponseHolder<>();
        pendingRequests.put(correlationId, holder);

        Message<NoteRequestTo> message = MessageBuilder
                .withPayload(request)
                .setHeader(KafkaHeaders.TOPIC, REQUEST_TOPIC)
                .setHeader(KafkaHeaders.REPLY_TOPIC, REPLY_TOPIC)
                .setHeader(KafkaHeaders.CORRELATION_ID, correlationId)
                .setHeader("operation", operation)
                .setHeader("responseType", responseType.getName())
                .build();

        try {
            kafkaTemplate.send(message).get(10, TimeUnit.SECONDS);

            if (!holder.latch.await(REPLY_TIMEOUT, TimeUnit.SECONDS)) {
                throw new TimeoutException("Timeout waiting for reply");
            }

            if (holder.response == null) {
                throw new RuntimeException("Received null response");
            }

            return responseType.cast(holder.response);
        } catch (Exception e) {
            pendingRequests.remove(correlationId);
            throw new RuntimeException("Failed to send/receive message", e);
        }
    }

    public List<NoteResponseTo> findAll() throws TimeoutException {
        return sendAndReceive(new NoteRequestTo(), "getAll", List.class);
    }

    public NoteResponseTo findById(Long id) throws TimeoutException {
        NoteRequestTo request = new NoteRequestTo();
        request.setId(id);
        return sendAndReceive(request, "getById", NoteResponseTo.class);
    }

    public NoteResponseTo save(NoteRequestTo dto) throws TimeoutException {
        return sendAndReceive(dto, "create", NoteResponseTo.class);
    }

    public NoteResponseTo update(NoteRequestTo dto) throws TimeoutException {
        return sendAndReceive(dto, "update", NoteResponseTo.class);
    }

    public void deleteById(Long id) throws TimeoutException {
        NoteRequestTo request = new NoteRequestTo();
        request.setId(id);
        sendAndReceive(request, "delete", NoteResponseTo.class);
    }

    private static class ResponseHolder<T> {
        private final CountDownLatch latch = new CountDownLatch(1);
        private T response;

        @SuppressWarnings("unchecked")
        public void setResponse(Object response) {
            this.response = (T) response;
        }
    }
}
