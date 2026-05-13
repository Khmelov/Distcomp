package com.example.demo.labrest.service;

import com.example.demo.labrest.dto.KafkaNoticeRequest;
import com.example.demo.labrest.dto.KafkaNoticeResponse;
import com.example.demo.labrest.kafka.KafkaProducer;
import com.example.demo.labrest.model.NoticeEntity;
import com.example.demo.labrest.repository.NoticeRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class DiscussionService {

    private final NoticeRepository noticeRepo;
    private final KafkaProducer kafkaProducer;

    private static final Set<String> STOP_WORDS = Set.of("spam", "bad", "offensive", "violation");

    public KafkaNoticeResponse processNotice(KafkaNoticeRequest request) {
        try {
            KafkaNoticeResponse response = switch (request.getOperation()) {
                case CREATE -> handleCreate(request);
                case READ -> handleRead(request);
                case UPDATE -> handleUpdate(request);
                case DELETE -> handleDelete(request);
                case READ_ALL_BY_TOPIC -> handleReadByTopic(request);
            };
            kafkaProducer.sendModerationResult(response);
            return response;
        } catch (Exception e) {
            var error = KafkaNoticeResponse.builder()
                    .correlationId(request.getCorrelationId())
                    .state(KafkaNoticeResponse.State.ERROR)
                    .reason(e.getMessage())
                    .build();
            kafkaProducer.sendModerationResult(error);
            return error;
        }
    }

    private KafkaNoticeResponse handleReadByTopic(KafkaNoticeRequest req) {
        List<NoticeEntity> notices = noticeRepo.findByTopicId(req.getTopicId());

        var responseData = notices.stream()
                .map(e -> KafkaNoticeResponse.NoticeData.builder()
                        .id(e.getId())
                        .topicId(e.getTopicId())
                        .content(e.getContent())
                        .build())
                .collect(Collectors.toList());

        return KafkaNoticeResponse.builder()
                .correlationId(req.getCorrelationId())
                .state(KafkaNoticeResponse.State.SUCCESS)
                .notices(responseData)
                .build();
    }

    private KafkaNoticeResponse handleCreate(KafkaNoticeRequest req) {
        var newState = STOP_WORDS.stream().anyMatch(req.getContent().toLowerCase()::contains) ? "DECLINE" : "APPROVE";

        var entity = NoticeEntity.builder()
                .id(req.getId())
                .topicId(req.getTopicId())
                .content(req.getContent())
                .country(req.getCountry())
                .state(newState)
                .created(LocalDateTime.now())
                .modified(LocalDateTime.now())
                .build();

        var saved = noticeRepo.save(entity);
        return buildResponse(req.getCorrelationId(), KafkaNoticeResponse.State.SUCCESS, saved, newState);
    }

    private KafkaNoticeResponse handleRead(KafkaNoticeRequest req) {
        log.debug("🔍 handleRead: id={}, topicId={}, content='{}'",
                req.getId(), req.getTopicId(), req.getContent());

        return noticeRepo.findById(req.getId())
                .map(entity -> {
                    log.debug("✅ Entity found: id={}, topicId={}, content='{}'",
                            entity.getId(), entity.getTopicId(), entity.getContent());

                    return buildResponse(req.getCorrelationId(), KafkaNoticeResponse.State.SUCCESS, entity, null);
                })
                .orElse(buildResponse(req.getCorrelationId(), KafkaNoticeResponse.State.NOT_FOUND, null, "Not found"));
    }

    private KafkaNoticeResponse handleUpdate(KafkaNoticeRequest req) {
        return noticeRepo.findById(req.getId())
                .map(e -> {
                    e.setContent(req.getContent());
                    e.setModified(LocalDateTime.now());
                    return buildResponse(req.getCorrelationId(), KafkaNoticeResponse.State.SUCCESS, noticeRepo.save(e), e.getState());
                })
                .orElse(buildResponse(req.getCorrelationId(), KafkaNoticeResponse.State.NOT_FOUND, null, "Not found"));
    }

    private KafkaNoticeResponse handleDelete(KafkaNoticeRequest req) {
        return noticeRepo.findById(req.getId())
                .map(entity -> {
                    try {
                        noticeRepo.deleteById(entity.getId());
                        return KafkaNoticeResponse.builder()
                                .correlationId(req.getCorrelationId())
                                .state(KafkaNoticeResponse.State.SUCCESS)
                                .build();
                    } catch (Exception e) {
                        log.error("Failed to delete notice id={}", req.getId(), e);
                        return KafkaNoticeResponse.builder()
                                .correlationId(req.getCorrelationId())
                                .state(KafkaNoticeResponse.State.ERROR)
                                .reason(e.getMessage())
                                .build();
                    }
                })
                .orElse(KafkaNoticeResponse.builder()
                        .correlationId(req.getCorrelationId())
                        .state(KafkaNoticeResponse.State.NOT_FOUND)
                        .reason("Notice not found")
                        .build());
    }

    private KafkaNoticeResponse buildResponse(String correlationId, KafkaNoticeResponse.State state, NoticeEntity entity, String reason) {
        var builder = KafkaNoticeResponse.builder()
                .correlationId(correlationId)
                .state(state);

        if (entity != null) {
            builder.id(entity.getId());
            builder.topicId(entity.getTopicId());
            builder.content(entity.getContent());
        }

        if (reason != null && !reason.equals("APPROVE") && !reason.equals("DECLINE")) {
            builder.reason(reason);
        }

        return builder.build();
    }

    public List<KafkaNoticeResponse> getAllNoticesAsResponses() {
        log.info("Getting all notices as responses");
        return noticeRepo.findAll().stream()
                .map(this::entityToResponse)
                .collect(Collectors.toList());
    }

    public List<KafkaNoticeResponse> getNoticesByTopicAsResponses(Long topicId) {
        log.info("Getting notices for topic {} as responses", topicId);
        return noticeRepo.findByTopicId(topicId).stream()
                .map(this::entityToResponse)
                .collect(Collectors.toList());
    }

    public Optional<KafkaNoticeResponse> getNoticeAsResponse(Long id) {
        log.info("Getting notice by id {} as response", id);
        return noticeRepo.findById(id)
                .map(this::entityToResponse);
    }

    public void deleteNotice(Long id) {
        log.info("Deleting notice by id {}", id);
        noticeRepo.deleteById(id);
    }

    private KafkaNoticeResponse entityToResponse(NoticeEntity entity) {
        return buildKafkaNoticeResponse(entity, entity.getState());
    }

    private KafkaNoticeResponse buildKafkaNoticeResponse(NoticeEntity entity, String state) {
        return KafkaNoticeResponse.builder()
                .id(entity.getId())
                .topicId(entity.getTopicId())
                .content(entity.getContent())
                .state(KafkaNoticeResponse.State.valueOf(entity.getState()))
                .reason(state.equals("DECLINE") ? "Prohibited content detected" : null)
                .build();
    }
}