package by.bsuir.discussion.services.impl;

import by.bsuir.discussion.domain.Comment;
import by.bsuir.discussion.dto.CommentActionDto;
import by.bsuir.discussion.dto.CommentActionTypeDto;
import by.bsuir.discussion.dto.CommentState;
import by.bsuir.discussion.dto.requests.CommentRequestDto;
import by.bsuir.discussion.dto.requests.converters.CommentRequestConverter;
import by.bsuir.discussion.dto.responses.CommentResponseDto;
import by.bsuir.discussion.dto.responses.converters.CollectionCommentResponseConverter;
import by.bsuir.discussion.dto.responses.converters.CommentResponseConverter;
import by.bsuir.discussion.exceptions.EntityExistsException;
import by.bsuir.discussion.exceptions.ErrorDto;
import by.bsuir.discussion.exceptions.Comments;
import by.bsuir.discussion.exceptions.NoEntityExistsException;
import by.bsuir.discussion.repositories.CommentRepository;
import by.bsuir.discussion.services.CommentService;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.validation.Valid;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Lazy;
import org.springframework.http.HttpStatus;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.annotation.Validated;

import java.util.List;
import java.util.Locale;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Validated
@Transactional(rollbackFor = {EntityExistsException.class, NoEntityExistsException.class})
public class CommentServiceImpl implements CommentService {

    private static final List<String> STOP_WORDS = List.of(
            "спам", "spam", "реклама", "реклам", "купить", "viagra", "казино"
    );

    private final CommentRepository commentRepository;
    private final CommentRequestConverter commentRequestConverter;
    private final CommentResponseConverter commentResponseConverter;
    private final CollectionCommentResponseConverter collectionCommentResponseConverter;
    private final ObjectMapper objectMapper;
    private final KafkaTemplate<String, CommentActionDto> kafkaCommentActionTemplate;

    @Value("${topic.commentChangeTopic}")
    private String commentChangeTopic;

    private CommentService commentService;

    @Autowired
    public void setCommentService(@Lazy CommentService commentService) {
        this.commentService = commentService;
    }

    @KafkaListener(topics = "${topic.inTopic}")
    @SendTo
    protected CommentActionDto receive(CommentActionDto commentActionDto) {
        try {
            return dispatchKafkaAction(commentActionDto);
        } catch (Exception e) {
            return CommentActionDto.builder().
                    action(commentActionDto.getAction()).
                    data(ErrorDto.builder().
                            code(HttpStatus.INTERNAL_SERVER_ERROR.value() + "00").
                            comment(e.getMessage() != null ? e.getMessage() : e.getClass().getSimpleName()).
                            build()).
                    build();
        }
    }

    private CommentActionDto dispatchKafkaAction(CommentActionDto commentActionDto) {
        switch (commentActionDto.getAction()) {
            case CREATE -> {
                try {
                    CommentRequestDto commentRequest = objectMapper.convertValue(commentActionDto.getData(),
                            CommentRequestDto.class);
                    return CommentActionDto.builder().
                            action(CommentActionTypeDto.CREATE).
                            data(commentService.create(commentRequest)).
                            build();
                } catch (EntityExistsException e) {
                    return CommentActionDto.builder().
                            action(CommentActionTypeDto.CREATE).
                            data(ErrorDto.builder().
                                    code(HttpStatus.BAD_REQUEST.value() + "00").
                                    comment(Comments.EntityExistsException).
                                    build()).
                            build();
                }
            }
            case READ -> {
                try {
                    Long id = toKafkaLongId(commentActionDto.getData());
                    Optional<CommentResponseDto> found = commentService.read(id);
                    if (found.isPresent()) {
                        return CommentActionDto.builder().
                                action(CommentActionTypeDto.READ).
                                data(found.get()).
                                build();
                    }
                    return CommentActionDto.builder().
                            action(CommentActionTypeDto.READ).
                            data(ErrorDto.builder().
                                    code(HttpStatus.BAD_REQUEST.value() + "00").
                                    comment(Comments.NoEntityExistsException).
                                    build()).
                            build();
                } catch (IllegalArgumentException e) {
                    return CommentActionDto.builder().
                            action(CommentActionTypeDto.READ).
                            data(ErrorDto.builder().
                                    code(HttpStatus.BAD_REQUEST.value() + "00").
                                    comment(Comments.NoEntityExistsException).
                                    build()).
                            build();
                }
            }
            case READ_ALL -> {
                return CommentActionDto.builder().
                        action(CommentActionTypeDto.READ_ALL).
                        data(commentService.readAll()).
                        build();
            }
            case UPDATE -> {
                try {
                    CommentRequestDto commentRequest = objectMapper.convertValue(commentActionDto.getData(),
                            CommentRequestDto.class);
                    return CommentActionDto.builder().
                            action(CommentActionTypeDto.UPDATE).
                            data(commentService.update(commentRequest)).
                            build();
                } catch (NoEntityExistsException e) {
                    return CommentActionDto.builder().
                            action(CommentActionTypeDto.UPDATE).
                            data(ErrorDto.builder().
                                    code(HttpStatus.BAD_REQUEST.value() + "00").
                                    comment(Comments.NoEntityExistsException).
                                    build()).
                            build();
                }
            }
            case DELETE -> {
                try {
                    Long id;
                    try {
                        id = toKafkaLongId(commentActionDto.getData());
                    } catch (IllegalArgumentException e) {
                        return CommentActionDto.builder().
                                action(CommentActionTypeDto.DELETE).
                                data(-1L).
                                build();
                    }
                    return CommentActionDto.builder().
                            action(CommentActionTypeDto.DELETE).
                            data(commentService.delete(id)).
                            build();
                } catch (NoEntityExistsException e) {
                    return CommentActionDto.builder().
                            action(CommentActionTypeDto.DELETE).
                            data(ErrorDto.builder().
                                    code(HttpStatus.BAD_REQUEST.value() + "00").
                                    comment(Comments.NoEntityExistsException).
                                    build()).
                            build();
                }
            }
        }
        return commentActionDto;
    }

    @Override
    @Validated
    public CommentResponseDto create(@Valid @NonNull CommentRequestDto dto) throws EntityExistsException {
        Optional<Comment> comment = dto.getId() == null ? Optional.empty() : commentRepository.findCommentById(dto.getId());
        if (comment.isEmpty()) {
            Comment entity = commentRequestConverter.fromDto(dto);
            if (dto.getId() == null) {
                entity.setId((long) (Math.random() * 2_000_000_000L) + 1);
            }
            entity.setState(CommentState.PENDING);
            entity.setState(moderateContent(entity.getContent()));
            CommentResponseDto commentResponseDto = commentResponseConverter.toDto(commentRepository.save(entity));
            CommentActionDto commentActionDto = CommentActionDto.builder().
                    action(CommentActionTypeDto.CREATE).
                    data(commentResponseDto).
                    build();
            ProducerRecord<String, CommentActionDto> record = new ProducerRecord<>(commentChangeTopic, null,
                    System.currentTimeMillis(), String.valueOf(commentActionDto.toString()),
                    commentActionDto);
            //kafkaCommentActionTemplate.send(record);
            return commentResponseDto;
        } else {
            throw new EntityExistsException(Comments.EntityExistsException);
        }
    }

    @Override
    public Optional<CommentResponseDto> read(@NonNull Long id) {
        return commentRepository.findCommentById(id).flatMap(author -> Optional.of(
                commentResponseConverter.toDto(author)));
    }

    @Override
    @Validated
    public CommentResponseDto update(@Valid @NonNull CommentRequestDto dto) throws NoEntityExistsException {
        Optional<Comment> comment = dto.getId() == null || commentRepository.findCommentByNewsIdAndId(
                dto.getNewsId(), dto.getId()).isEmpty() ?
                Optional.empty() : Optional.of(commentRequestConverter.fromDto(dto));
        Comment toSave = comment.orElseThrow(() ->
                new NoEntityExistsException(Comments.NoEntityExistsException));
        toSave.setState(CommentState.PENDING);
        toSave.setState(moderateContent(toSave.getContent()));
        CommentResponseDto commentResponseDto = commentResponseConverter.toDto(commentRepository.save(toSave));
        CommentActionDto commentActionDto = CommentActionDto.builder().
                action(CommentActionTypeDto.UPDATE).
                data(commentResponseDto).
                build();
        ProducerRecord<String, CommentActionDto> record = new ProducerRecord<>(commentChangeTopic, null,
                System.currentTimeMillis(), String.valueOf(commentActionDto.toString()),
                commentActionDto);
        //kafkaCommentActionTemplate.send(record);
        return commentResponseDto;
    }

    @Override
    public Long delete(@NonNull Long id) throws NoEntityExistsException {
        Optional<Comment> comment = commentRepository.findCommentById(id);
        if (comment.isPresent()) {
            commentRepository.deleteCommentByNewsIdAndId(comment.map(Comment::getNewsId).orElseThrow(() ->
                    new NoEntityExistsException(Comments.NoEntityExistsException)), comment.map(Comment::getId).
                    orElseThrow(() -> new NoEntityExistsException(Comments.NoEntityExistsException)));
            CommentActionDto commentActionDto = CommentActionDto.builder().
                    action(CommentActionTypeDto.DELETE).
                    data(String.valueOf(id)).
                    build();
            ProducerRecord<String, CommentActionDto> record = new ProducerRecord<>(commentChangeTopic, null,
                    System.currentTimeMillis(), String.valueOf(commentActionDto.toString()),
                    commentActionDto);
            //kafkaCommentActionTemplate.send(record);
            return comment.get().getId();
        }
        return -1L;
    }

    @Override
    public List<CommentResponseDto> readAll() {
        return collectionCommentResponseConverter.toListDto(commentRepository.findAll());
    }

    /** Kafka payloads may deserialize numeric ids as Integer/Long, not String. */
    private static Long toKafkaLongId(Object data) {
        if (data instanceof Number n) {
            return n.longValue();
        }
        if (data instanceof String s) {
            return Long.parseLong(s.trim());
        }
        throw new IllegalArgumentException("Comment id must be numeric, got " +
                (data == null ? "null" : data.getClass().getSimpleName()));
    }

    private static CommentState moderateContent(String content) {
        if (content == null || content.isBlank()) {
            return CommentState.DECLINE;
        }
        String lower = content.toLowerCase(Locale.ROOT);
        for (String word : STOP_WORDS) {
            if (lower.contains(word)) {
                return CommentState.DECLINE;
            }
        }
        return CommentState.APPROVE;
    }
}
