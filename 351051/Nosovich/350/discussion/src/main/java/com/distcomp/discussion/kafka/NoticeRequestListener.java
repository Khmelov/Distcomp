package com.distcomp.discussion.kafka;

import com.distcomp.common.kafka.NoticeKafkaRequest;
import com.distcomp.common.kafka.NoticeKafkaResponse;
import com.distcomp.common.notice.NoticeDto;
import com.distcomp.discussion.dto.NoticePage;
import com.distcomp.discussion.dto.NoticeRequestTo;
import com.distcomp.discussion.exception.ApiException;
import com.distcomp.discussion.exception.ValidationException;
import com.distcomp.discussion.service.NoticeService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

/**
 * Consumes notice requests from {@code InTopic} and publishes the result to
 * {@code OutTopic}.
 *
 * <p>Every request is answered, including failed ones, otherwise the publisher
 * would wait until its reply timeout. The response uses the issue identifier as
 * the Kafka key, so all messages of one discussion stay in the same partition.</p>
 */
@Service
public class NoticeRequestListener {

    private static final Logger log = LoggerFactory.getLogger(NoticeRequestListener.class);

    private final NoticeService noticeService;

    private final KafkaTemplate<String, Object> kafkaTemplate;

    private final String outTopic;

    public NoticeRequestListener(NoticeService noticeService,
                                 KafkaTemplate<String, Object> kafkaTemplate,
                                 @Value("${app.kafka.out-topic}") String outTopic) {
        this.noticeService = noticeService;
        this.kafkaTemplate = kafkaTemplate;
        this.outTopic = outTopic;
    }

    @KafkaListener(topics = "${app.kafka.in-topic}", groupId = "${app.kafka.discussion-group}")
    public void onRequest(NoticeKafkaRequest request) {
        NoticeKafkaResponse response;
        try {
            response = handle(request).withRequestId(request.requestId());
        } catch (ApiException exception) {
            response = NoticeKafkaResponse.error(exception.getCode(), exception.getMessage())
                    .withRequestId(request.requestId());
        } catch (Exception exception) {
            log.error("Failed to process Kafka request {}", request.requestId(), exception);
            response = NoticeKafkaResponse.error("50001", "Unexpected server error")
                    .withRequestId(request.requestId());
        }

        kafkaTemplate.send(outTopic, partitionKey(request, response), response)
                .whenComplete((result, error) -> {
                    if (error != null) {
                        log.error("Failed to send reply for request {}", request.requestId(), error);
                    }
                });
    }

    private NoticeKafkaResponse handle(NoticeKafkaRequest request) {
        if (request.operation() == null) {
            throw new ValidationException("operation", "must be specified");
        }
        return switch (request.operation()) {
            case CREATE -> NoticeKafkaResponse.ok(noticeService.create(
                    new NoticeRequestTo(null, request.issueId(), request.content(), null)));
            case GET -> NoticeKafkaResponse.ok(noticeService.findById(request.id()));
            case FIND_ALL -> {
                NoticePage page = noticeService.findAll(request.issueId(), request.page(),
                        request.size(), request.sort());
                yield NoticeKafkaResponse.ok(page.content(), page.total());
            }
            case UPDATE -> NoticeKafkaResponse.ok(noticeService.update(request.id(),
                    new NoticeRequestTo(request.id(), request.issueId(), request.content(), null)));
            case DELETE -> {
                noticeService.delete(request.id());
                yield NoticeKafkaResponse.empty();
            }
            case DELETE_BY_ISSUE -> {
                noticeService.deleteByIssueId(request.issueId());
                yield NoticeKafkaResponse.empty();
            }
        };
    }

    private String partitionKey(NoticeKafkaRequest request, NoticeKafkaResponse response) {
        NoticeDto notice = response.notice();
        Long issueId = request.issueId() != null
                ? request.issueId()
                : notice == null ? null : notice.issueId();
        return issueId == null ? request.partitionKey() : "issue-" + issueId;
    }
}
