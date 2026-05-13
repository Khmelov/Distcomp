package by.bsuir.distcomp.service;

import by.bsuir.distcomp.dto.CommentDto;
import by.bsuir.distcomp.dto.CommentState;
import by.bsuir.distcomp.exception.ApiException;
import by.bsuir.distcomp.cache.RedisCacheService;
import by.bsuir.distcomp.kafka.CommentKafkaRequest;
import by.bsuir.distcomp.kafka.CommentKafkaResponse;
import by.bsuir.distcomp.kafka.CommentOperation;
import by.bsuir.distcomp.repository.IssueRepository;
import java.time.Duration;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import java.util.concurrent.atomic.AtomicLong;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@Service
public class CommentProxyService implements CrudService<CommentDto> {
    private static final Duration REPLY_TIMEOUT = Duration.ofSeconds(1);

    private final IssueRepository issueRepository;
    private final KafkaTemplate<String, Object> kafkaTemplate;
    private final RedisCacheService cache;
    private final String inTopic;
    private final ConcurrentMap<String, CompletableFuture<CommentKafkaResponse>> replies = new ConcurrentHashMap<>();
    private final AtomicLong idSequence = new AtomicLong(System.currentTimeMillis());

    public CommentProxyService(
            IssueRepository issueRepository,
            KafkaTemplate<String, Object> kafkaTemplate,
            RedisCacheService cache,
            @Value("${kafka.topics.in}") String inTopic) {
        this.issueRepository = issueRepository;
        this.kafkaTemplate = kafkaTemplate;
        this.cache = cache;
        this.inTopic = inTopic;
    }

    @Override
    public CommentDto create(CommentDto dto) {
        validateIssue(dto.issueId());
        long id = idSequence.incrementAndGet();
        CommentDto pending = new CommentDto(id, dto.issueId(), dto.content(), CommentState.PENDING);
        send(new CommentKafkaRequest(newRequestId(), CommentOperation.CREATE, id, pending, 0, 0));
        cache.evict(key(id));
        cache.evictByPrefix(listPrefix());
        return pending;
    }

    @Override
    public CommentDto get(Long id) {
        CommentDto cached = cache.get(key(id), CommentDto.class);
        if (cached != null) {
            return cached;
        }
        CommentKafkaResponse response = request(new CommentKafkaRequest(newRequestId(), CommentOperation.GET, id, null, 0, 0));
        cache.put(key(id), response.comment());
        return response.comment();
    }

    @Override
    public List<CommentDto> findAll(Pageable pageable) {
        String key = listKey(pageable);
        List<CommentDto> cached = cache.getList(key, CommentDto.class);
        if (cached != null) {
            return cached;
        }
        CommentKafkaResponse response = request(new CommentKafkaRequest(
                newRequestId(), CommentOperation.FIND_ALL, null, null, pageable.getPageNumber(), pageable.getPageSize()));
        List<CommentDto> comments = response.comments() == null ? List.of() : response.comments();
        cache.put(key, comments);
        return comments;
    }

    @Override
    public CommentDto update(Long id, CommentDto dto) {
        validateIssue(dto.issueId());
        CommentDto pending = new CommentDto(id, dto.issueId(), dto.content(), CommentState.PENDING);
        CommentKafkaResponse response = request(new CommentKafkaRequest(
                newRequestId(), CommentOperation.UPDATE, id, pending, 0, 0));
        cache.put(key(id), response.comment());
        cache.evictByPrefix(listPrefix());
        return response.comment();
    }

    @Override
    public void delete(Long id) {
        request(new CommentKafkaRequest(newRequestId(), CommentOperation.DELETE, id, null, 0, 0));
        cache.evict(key(id));
        cache.evictByPrefix(listPrefix());
    }

    @KafkaListener(topics = "${kafka.topics.out}", groupId = "${kafka.groups.publisher}")
    public void onReply(CommentKafkaResponse response) {
        CompletableFuture<CommentKafkaResponse> future = replies.remove(response.requestId());
        if (future != null) {
            future.complete(response);
        }
    }

    private CommentKafkaResponse request(CommentKafkaRequest request) {
        CompletableFuture<CommentKafkaResponse> future = new CompletableFuture<>();
        replies.put(request.requestId(), future);
        send(request);
        try {
            CommentKafkaResponse response = future.get(REPLY_TIMEOUT.toMillis(), TimeUnit.MILLISECONDS);
            if (!response.success()) {
                throw toApiException(response);
            }
            return response;
        } catch (TimeoutException ex) {
            replies.remove(request.requestId());
            throw new ApiException(HttpStatus.SERVICE_UNAVAILABLE, "50301", "Discussion service timeout");
        } catch (InterruptedException ex) {
            Thread.currentThread().interrupt();
            throw new ApiException(HttpStatus.SERVICE_UNAVAILABLE, "50302", "Discussion service interrupted");
        } catch (ApiException ex) {
            throw ex;
        } catch (Exception ex) {
            replies.remove(request.requestId());
            throw new ApiException(HttpStatus.SERVICE_UNAVAILABLE, "50303", "Discussion service unavailable");
        }
    }

    private void send(CommentKafkaRequest request) {
        String key = request.comment() != null && request.comment().issueId() != null
                ? request.comment().issueId().toString()
                : String.valueOf(request.id());
        try {
            kafkaTemplate.send(inTopic, key, request).get(REPLY_TIMEOUT.toMillis(), TimeUnit.MILLISECONDS);
        } catch (Exception ex) {
            throw new ApiException(HttpStatus.SERVICE_UNAVAILABLE, "50304", "Kafka broker unavailable");
        }
    }

    private void validateIssue(Long issueId) {
        if (issueId == null || !issueRepository.existsById(issueId)) {
            throw new ApiException(HttpStatus.BAD_REQUEST, "40003", "Issue association not found");
        }
    }

    private ApiException toApiException(CommentKafkaResponse response) {
        if ("40403".equals(response.errorCode())) {
            return new ApiException(HttpStatus.NOT_FOUND, response.errorCode(), response.errorMessage());
        }
        return new ApiException(HttpStatus.BAD_REQUEST, response.errorCode(), response.errorMessage());
    }

    private String newRequestId() {
        return UUID.randomUUID().toString();
    }

    private String key(Long id) {
        return "comment:" + id;
    }

    private String listPrefix() {
        return "comment:list:";
    }

    private String listKey(Pageable pageable) {
        return listPrefix() + pageable.getPageNumber() + ":" + pageable.getPageSize();
    }
}
