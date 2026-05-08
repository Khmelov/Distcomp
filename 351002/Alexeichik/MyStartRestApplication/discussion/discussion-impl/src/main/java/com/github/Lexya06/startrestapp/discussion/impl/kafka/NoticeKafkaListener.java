package com.github.Lexya06.startrestapp.discussion.impl.kafka;

import com.github.Lexya06.startrestapp.discussion.api.dto.notice.*;
import com.github.Lexya06.startrestapp.discussion.impl.config.KafkaConfig;
import com.github.Lexya06.startrestapp.discussion.impl.model.entity.realization.Notice;
import com.github.Lexya06.startrestapp.discussion.impl.service.customexception.MyEntityNotFoundException;
import com.github.Lexya06.startrestapp.discussion.impl.service.realization.NoticeService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.KafkaHeaders;
import org.springframework.messaging.Message;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.List;

@Slf4j
@Component
@RequiredArgsConstructor
public class NoticeKafkaListener {

    private final NoticeService noticeService;

    @KafkaListener(topics = KafkaConfig.IN_TOPIC, groupId = "discussion-group")
    @SendTo(KafkaConfig.OUT_TOPIC)
    public Message<KafkaNoticeResponseMessage> listen(KafkaNoticeMessage message,
                                                     @Header(KafkaHeaders.CORRELATION_ID) byte[] correlationId) {
        log.debug("Received Kafka message: {}", message);
        KafkaNoticeResponseMessage response;
        try {
            response = switch (message.getOperation()) {
                case POST -> handlePost(message);
                case GET -> handleGet(message);
                case PUT -> handlePut(message);
                case DELETE -> handleDelete(message);
                case GET_ALL -> handleGetAll(message);
            };
        } catch (Exception e) {
            log.error("Error processing Kafka message: {}", e.getMessage());
            KafkaNoticeResponseMessage.KafkaNoticeResponseMessageBuilder responseBuilder = KafkaNoticeResponseMessage.builder()
                    .errorType(e.getClass().getSimpleName())
                    .errorMessage(e.getMessage());
            
            if (e instanceof MyEntityNotFoundException) {
                responseBuilder.errorKey(((MyEntityNotFoundException) e).getKey());
            }
            
            response = responseBuilder.build();
        }

        return MessageBuilder.withPayload(response)
                .setHeader(KafkaHeaders.CORRELATION_ID, correlationId)
                .build();
    }

    private KafkaNoticeResponseMessage handleGet(KafkaNoticeMessage message) {
        NoticeResponseTo response;
        if (message.getKeyDto() != null) {
            response = noticeService.getEntityById(message.getKeyDto());
        } else {
            response = noticeService.getEntityByIdId(message.getId());
        }
        return KafkaNoticeResponseMessage.builder().responsePayload(response).build();
    }

    private KafkaNoticeResponseMessage handlePost(KafkaNoticeMessage message) {
        // Листенер больше не вызывает moderate()
        NoticeResponseTo response = noticeService.createFromKafka(
                message.getRequestPayload(),
                message.getId()
        );

        return KafkaNoticeResponseMessage.builder()
                .responsePayload(response)
                .build();
    }

    private KafkaNoticeResponseMessage handlePut(KafkaNoticeMessage message) {
        // Листенер просто пробрасывает запрос в сервис
        NoticeResponseTo response = noticeService.updateFromKafka(
                message.getRequestPayload(),
                message.getId(),
                message.getKeyDto()
        );

        return KafkaNoticeResponseMessage.builder()
                .responsePayload(response)
                .build();
    }

    private KafkaNoticeResponseMessage handleDelete(KafkaNoticeMessage message) {
        if (message.getKeyDto() != null) {
            noticeService.deleteEntityById(message.getKeyDto());
        } else {
            noticeService.deleteEntityByIdId(message.getId());
        }
        return KafkaNoticeResponseMessage.builder().build();
    }

    private KafkaNoticeResponseMessage handleGetAll(KafkaNoticeMessage message) {
        List<NoticeResponseTo> response = noticeService.getAllEntitiesByCriteria(message.getCriteria()).getData();
        return KafkaNoticeResponseMessage.builder().responseListPayload(response).build();
    }


}
