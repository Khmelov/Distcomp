package com.lizaveta.discussion.kafka;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.lizaveta.discussion.exception.ResourceNotFoundException;
import com.lizaveta.discussion.exception.ValidationException;
import com.lizaveta.discussion.service.NoticeService;
import com.lizaveta.notebook.kafka.NoticeInMessageType;
import com.lizaveta.notebook.kafka.NoticeKafkaInEnvelope;
import com.lizaveta.notebook.kafka.NoticeKafkaOutEnvelope;
import com.lizaveta.notebook.model.dto.request.NoticeRequestTo;
import com.lizaveta.notebook.model.dto.response.NoticeResponseTo;
import com.lizaveta.notebook.model.dto.response.PageResponseTo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

@Component
public class NoticeInTopicListener {

    private static final Logger LOG = LoggerFactory.getLogger(NoticeInTopicListener.class);

    private final NoticeService noticeService;
    private final ObjectMapper objectMapper;
    private final KafkaTemplate<String, String> kafkaTemplate;
    private final String outTopic;

    public NoticeInTopicListener(
            final NoticeService noticeService,
            final ObjectMapper objectMapper,
            final KafkaTemplate<String, String> kafkaTemplate,
            @Value("${kafka.topic.out-topic}") final String outTopic) {
        this.noticeService = noticeService;
        this.objectMapper = objectMapper;
        this.kafkaTemplate = kafkaTemplate;
        this.outTopic = outTopic;
    }

    @KafkaListener(topics = "${kafka.topic.in-topic}", groupId = "${spring.kafka.consumer.group-id}")
    public void onMessage(final String value) {
        try {
            NoticeKafkaInEnvelope envelope = objectMapper.readValue(value, NoticeKafkaInEnvelope.class);
            if (envelope.type() == NoticeInMessageType.DRAFT_CREATE) {
                handleDraft(envelope);
                return;
            }
            if (envelope.correlationId() == null) {
                return;
            }
            handleSync(envelope);
        } catch (Exception ex) {
            LOG.error("Kafka in message failed", ex);
        }
    }

    private void handleDraft(final NoticeKafkaInEnvelope envelope) {
        try {
            JsonNode p = envelope.payload();
            long id = p.get("id").asLong();
            long storyId = p.get("storyId").asLong();
            String content = p.get("content").asText();
            noticeService.createFromKafkaDraft(id, storyId, content);
        } catch (Exception ex) {
            LOG.error("Draft processing failed", ex);
        }
    }

    private void handleSync(final NoticeKafkaInEnvelope envelope) throws Exception {
        String cid = envelope.correlationId();
        try {
            switch (envelope.type()) {
                case GET_BY_ID -> handleGetById(cid, envelope.payload());
                case GET_ALL_LIST -> sendSuccess(cid, noticeService.findAll());
                case GET_ALL_PAGE -> handlePage(cid, envelope.payload());
                case GET_BY_STORY -> handleByStory(cid, envelope.payload());
                case UPDATE -> handleUpdate(cid, envelope.payload());
                case DELETE -> handleDelete(cid, envelope.payload());
                default -> sendError(cid, 40001, "Unsupported message type");
            }
        } catch (ResourceNotFoundException ex) {
            sendError(cid, ex.getErrorCode(), ex.getMessage());
        } catch (ValidationException ex) {
            sendError(cid, ex.getErrorCode(), ex.getMessage());
        }
    }

    private void handleGetById(final String cid, final JsonNode payload) throws Exception {
        long id = payload.get("id").asLong();
        sendSuccess(cid, noticeService.findById(id));
    }

    private void handlePage(final String cid, final JsonNode p) throws Exception {
        int page = p.has("page") ? p.get("page").asInt() : 0;
        int size = p.has("size") ? p.get("size").asInt() : 20;
        String sortBy = p.has("sortBy") && !p.get("sortBy").isNull() ? p.get("sortBy").asText() : null;
        String sortOrder = p.has("sortOrder") ? p.get("sortOrder").asText() : "asc";
        PageResponseTo<NoticeResponseTo> pageRes = noticeService.findAll(page, size, sortBy, sortOrder);
        sendSuccess(cid, pageRes);
    }

    private void handleByStory(final String cid, final JsonNode payload) throws Exception {
        long storyId = payload.get("storyId").asLong();
        sendSuccess(cid, noticeService.findByStoryId(storyId));
    }

    private void handleUpdate(final String cid, final JsonNode payload) throws Exception {
        long id = payload.get("id").asLong();
        long storyId = payload.get("storyId").asLong();
        String content = payload.get("content").asText();
        NoticeRequestTo req = new NoticeRequestTo(storyId, content);
        sendSuccess(cid, noticeService.update(id, req));
    }

    private void handleDelete(final String cid, final JsonNode payload) throws Exception {
        long id = payload.get("id").asLong();
        noticeService.deleteById(id);
        sendSuccess(cid, null);
    }

    private void sendSuccess(final String correlationId, final Object payload) throws Exception {
        JsonNode node = payload == null ? null : objectMapper.valueToTree(payload);
        NoticeKafkaOutEnvelope out = new NoticeKafkaOutEnvelope(correlationId, true, null, null, node);
        kafkaTemplate.send(outTopic, correlationId, objectMapper.writeValueAsString(out)).get();
    }

    private void sendError(final String correlationId, final int code, final String message) throws Exception {
        NoticeKafkaOutEnvelope out = new NoticeKafkaOutEnvelope(correlationId, false, code, message, null);
        kafkaTemplate.send(outTopic, correlationId, objectMapper.writeValueAsString(out)).get();
    }
}
