package by.bsuir.publisher.services.impl;

import by.bsuir.publisher.dto.CommentActionDto;
import by.bsuir.publisher.dto.CommentActionTypeDto;
import by.bsuir.publisher.dto.requests.CommentRequestDto;
import by.bsuir.publisher.dto.responses.CommentResponseDto;
import by.bsuir.publisher.dto.responses.NewsResponseDto;
import by.bsuir.publisher.dto.responses.converters.CommentResponseConverter;
import by.bsuir.publisher.exceptions.*;
import by.bsuir.publisher.repositories.CommentCacheRepository;
import by.bsuir.publisher.services.CommentService;
import by.bsuir.publisher.services.NewsService;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.ObjectUtils;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.common.header.internals.RecordHeader;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.requestreply.ReplyingKafkaTemplate;
import org.springframework.kafka.requestreply.RequestReplyFuture;
import org.springframework.kafka.support.KafkaHeaders;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.annotation.Validated;

import org.springframework.http.HttpStatus;

import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

@Service
@RequiredArgsConstructor
@Validated
@Transactional(rollbackFor = {ServiceException.class})
public class CommentServiceImpl implements CommentService {
    private final ObjectMapper objectMapper;
    private final CommentResponseConverter commentResponseConverter;
    private final CommentCacheRepository commentCacheRepository;
    private final NewsService newsService;
    private final ReplyingKafkaTemplate<String, CommentActionDto, CommentActionDto> replyingKafkaTemplate;

    @Value("${topic.inTopic}")
    private String inTopic;

    @Value("${topic.outTopic}")
    private String outTopic;

    @Value("${app.kafka.request-reply-timeout-ms:30000}")
    private long kafkaRequestReplyTimeoutMs;

    @KafkaListener(topics = "${topic.commentChangeTopic}")
    protected void receiveCommentChange(CommentActionDto commentActionDto) {
        switch (commentActionDto.getAction()) {
            case CREATE, UPDATE -> {
                CommentResponseDto commentResponseDto = objectMapper.
                        convertValue(commentActionDto.getData(), CommentResponseDto.class);
                commentCacheRepository.save(commentResponseConverter.fromDto(commentResponseDto));
            }
            case DELETE -> {
                Long id = toKafkaLongId(commentActionDto.getData());
                commentCacheRepository.deleteById(id);
            }
        }
    }

    protected CommentActionDto sendCommentAction(CommentActionDto commentActionDto) throws ServiceException {
        String partitionKey = kafkaPartitionKey(commentActionDto);
        ProducerRecord<String, CommentActionDto> record = new ProducerRecord<>(inTopic, partitionKey,
                commentActionDto);
        record.headers().add(new RecordHeader(KafkaHeaders.REPLY_TOPIC, outTopic.getBytes(StandardCharsets.UTF_8)));
        RequestReplyFuture<String, CommentActionDto, CommentActionDto> response = replyingKafkaTemplate.sendAndReceive(record);
        try {
            return response.orTimeout(kafkaRequestReplyTimeoutMs, TimeUnit.MILLISECONDS).join().value();
        } catch (Exception e) {
            Throwable cause = e.getCause() != null ? e.getCause() : e;
            String msg = cause instanceof TimeoutException ? "Kafka reply timed out; is discussion running?" : cause.getMessage();
            throw new ServiceException(ErrorDto.builder().
                    code(HttpStatus.SERVICE_UNAVAILABLE.value() + "00").
                    comment(msg != null ? msg : "Kafka request failed").
                    build());
        }
    }

    private String kafkaPartitionKey(CommentActionDto dto) {
        try {
            return switch (dto.getAction()) {
                case CREATE, UPDATE -> {
                    CommentRequestDto req = objectMapper.convertValue(dto.getData(), CommentRequestDto.class);
                    yield req.getNewsId() != null ? String.valueOf(req.getNewsId()) : "0";
                }
                case READ, DELETE -> {
                    Long id = toKafkaLongId(dto.getData());
                    yield commentCacheRepository.findById(id).
                            map(c -> String.valueOf(c.getTweetId())).
                            orElseGet(() -> String.valueOf(id));
                }
                case READ_ALL -> "__read_all__";
            };
        } catch (RuntimeException ex) {
            return "0";
        }
    }

    @Override
    public CommentResponseDto create(@NonNull CommentRequestDto dto) throws ServiceException {
        Optional<NewsResponseDto> news = newsService.read(dto.getNewsId());
        if (news.isEmpty()) {
            return null;
        }
        CommentActionDto action = sendCommentAction(CommentActionDto.builder().
                action(CommentActionTypeDto.CREATE).
                data(dto).
                build());
        CommentResponseDto response = objectMapper.convertValue(action.getData(), CommentResponseDto.class);
        if (ObjectUtils.allNull(response.getId(), response.getNewsId(), response.getContent())) {
            throw new ServiceException(objectMapper.convertValue(action.getData(), ErrorDto.class));
        }
        commentCacheRepository.save(commentResponseConverter.fromDto(response));
        return response;
    }

    @Override
    public Optional<CommentResponseDto> read(@NonNull Long uuid) throws ServiceException {
        Optional<CommentResponseDto> fromCache = commentCacheRepository.findById(uuid).map(commentResponseConverter::toDto);
        if (fromCache.isPresent()) {
            return fromCache;
        }
        CommentActionDto action = sendCommentAction(CommentActionDto.builder().
                action(CommentActionTypeDto.READ).
                data(String.valueOf(uuid)).
                build());
        Object data = action.getData();
        if (data == null) {
            return Optional.empty();
        }
        if (data instanceof java.util.Map<?, ?> map && map.containsKey("code")) {
            return Optional.empty();
        }
        return Optional.of(objectMapper.convertValue(data, CommentResponseDto.class));
    }

    @Override
    @SuppressWarnings({"unchecked", "rawtypes"})
    public List<CommentResponseDto> readAll() throws ServiceException {
        CommentActionDto action = sendCommentAction(CommentActionDto.builder().
                action(CommentActionTypeDto.READ_ALL).
                build());
        Object data = action.getData();
        if (data == null) {
            return List.of();
        }
        if (!(data instanceof List<?> list)) {
            throw new ServiceException(objectMapper.convertValue(data, ErrorDto.class));
        }
        if (list.isEmpty()) {
            return List.of();
        }
        return list.stream().
                map(v -> objectMapper.convertValue(v, CommentResponseDto.class)).
                toList();
    }

    @Override
    public CommentResponseDto update(@NonNull CommentRequestDto dto) throws ServiceException {
        CommentActionDto action = sendCommentAction(CommentActionDto.builder().
                action(CommentActionTypeDto.UPDATE).
                data(dto).
                build());
        CommentResponseDto response = objectMapper.convertValue(action.getData(), CommentResponseDto.class);
        if (ObjectUtils.allNull(response.getId(), response.getContent(), response.getNewsId())) {
            throw new ServiceException(objectMapper.convertValue(action.getData(), ErrorDto.class));
        }
        commentCacheRepository.save(commentResponseConverter.fromDto(response));
        return response;
    }

    @Override
    public Long delete(@NonNull Long uuid) throws ServiceException {
        CommentActionDto action = sendCommentAction(CommentActionDto.builder().
                action(CommentActionTypeDto.DELETE).
                data(String.valueOf(uuid)).
                build());
        Object data = action.getData();
        if (data instanceof java.util.Map<?, ?> map && map.containsKey("code")) {
            throw new ServiceException(objectMapper.convertValue(data, ErrorDto.class));
        }
        Long o = toKafkaLongId(data);
        if (o != -1L) commentCacheRepository.deleteById(uuid);
        return o;
    }

    private static Long toKafkaLongId(Object data) {
        if (data instanceof Number n) {
            return n.longValue();
        }
        if (data instanceof String s) {
            return Long.parseLong(s.trim());
        }
        throw new IllegalArgumentException("Expected numeric id in Kafka payload");
    }
}
