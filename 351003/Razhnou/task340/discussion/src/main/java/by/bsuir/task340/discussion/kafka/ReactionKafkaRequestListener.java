package by.bsuir.task340.discussion.kafka;

import by.bsuir.task340.discussion.dto.request.ReactionRequestTo;
import by.bsuir.task340.discussion.dto.response.ReactionResponseTo;
import by.bsuir.task340.discussion.exception.ApiException;
import by.bsuir.task340.discussion.kafka.dto.ReactionKafkaRequest;
import by.bsuir.task340.discussion.kafka.dto.ReactionKafkaResponse;
import by.bsuir.task340.discussion.kafka.dto.ReactionOperation;
import by.bsuir.task340.discussion.kafka.dto.ReactionPayload;
import by.bsuir.task340.discussion.service.ReactionService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class ReactionKafkaRequestListener {
    private final ReactionService reactionService;
    private final KafkaTemplate<String, String> kafkaTemplate;
    private final ObjectMapper objectMapper;
    private final String outTopic;

    public ReactionKafkaRequestListener(
            ReactionService reactionService,
            KafkaTemplate<String, String> kafkaTemplate,
            ObjectMapper objectMapper,
            @Value("${app.kafka.reaction.out-topic}") String outTopic
    ) {
        this.reactionService = reactionService;
        this.kafkaTemplate = kafkaTemplate;
        this.objectMapper = objectMapper;
        this.outTopic = outTopic;
    }

    @KafkaListener(
            topics = "${app.kafka.reaction.in-topic}",
            containerFactory = "discussionKafkaListenerContainerFactory"
    )
    public void listen(ConsumerRecord<String, String> record) throws Exception {
        ReactionKafkaRequest request = objectMapper.readValue(record.value(), ReactionKafkaRequest.class);
        ReactionKafkaResponse response = process(request);
        kafkaTemplate.send(outTopic, resolveReplyKey(record.key(), request, response), objectMapper.writeValueAsString(response));
    }

    private ReactionKafkaResponse process(ReactionKafkaRequest request) {
        try {
            return switch (request.operation()) {
                case CREATE -> success(request.requestId(), HttpStatus.CREATED.value(), reactionService.createFromTransport(toRequest(request.payload())), null);
                case READ_ONE -> success(request.requestId(), HttpStatus.OK.value(), reactionService.findById(request.reactionId()), null);
                case READ_ALL -> success(request.requestId(), HttpStatus.OK.value(), null, reactionService.findAll());
                case UPDATE -> success(request.requestId(), HttpStatus.OK.value(), reactionService.update(toRequest(request.payload())), null);
                case DELETE -> {
                    reactionService.delete(request.reactionId());
                    yield new ReactionKafkaResponse(request.requestId(), true, HttpStatus.NO_CONTENT.value(), null, null, null, null);
                }
            };
        } catch (ApiException exception) {
            return new ReactionKafkaResponse(
                    request.requestId(),
                    false,
                    exception.getStatus().value(),
                    exception.getErrorCode(),
                    exception.getMessage(),
                    null,
                    null
            );
        } catch (Exception exception) {
            return new ReactionKafkaResponse(
                    request.requestId(),
                    false,
                    HttpStatus.INTERNAL_SERVER_ERROR.value(),
                    50000,
                    "Internal server error",
                    null,
                    null
            );
        }
    }

    private ReactionKafkaResponse success(
            String requestId,
            int status,
            ReactionResponseTo payload,
            List<ReactionResponseTo> payloadList
    ) {
        return new ReactionKafkaResponse(
                requestId,
                true,
                status,
                null,
                null,
                payload == null ? null : toPayload(payload),
                payloadList == null ? null : payloadList.stream().map(this::toPayload).toList()
        );
    }

    private ReactionRequestTo toRequest(ReactionPayload payload) {
        return new ReactionRequestTo(payload.id(), payload.tweetId(), payload.content(), payload.state());
    }

    private ReactionPayload toPayload(ReactionResponseTo response) {
        return new ReactionPayload(response.id(), response.tweetId(), response.content(), response.state());
    }

    private String resolveReplyKey(String originalKey, ReactionKafkaRequest request, ReactionKafkaResponse response) {
        if (response.payload() != null && response.payload().tweetId() != null) {
            return String.valueOf(response.payload().tweetId());
        }
        if (request.tweetIdKey() != null) {
            return String.valueOf(request.tweetIdKey());
        }
        if (originalKey != null && !originalKey.isBlank()) {
            return originalKey;
        }
        return request.operation() == ReactionOperation.READ_ALL ? "all" : String.valueOf(request.reactionId());
    }
}
