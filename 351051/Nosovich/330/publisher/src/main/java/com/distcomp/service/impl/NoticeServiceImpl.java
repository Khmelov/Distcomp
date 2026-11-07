package com.distcomp.service.impl;

import com.distcomp.cache.CacheService;
import com.distcomp.client.DiscussionNoticeClient;
import com.distcomp.common.kafka.NoticeKafkaRequest;
import com.distcomp.common.kafka.NoticeKafkaResponse;
import com.distcomp.common.notice.NoticeDto;
import com.distcomp.dto.filter.NoticeFilter;
import com.distcomp.dto.request.NoticeRequestTo;
import com.distcomp.dto.response.NoticeResponseTo;
import com.distcomp.exception.ApiException;
import com.distcomp.exception.EntityNotFoundException;
import com.distcomp.exception.KafkaTransportException;
import com.distcomp.exception.RemoteDiscussionException;
import com.distcomp.exception.ValidationException;
import com.distcomp.kafka.NoticeKafkaGateway;
import com.distcomp.pagination.PagedResult;
import com.distcomp.repository.IssueRepository;
import com.distcomp.service.NoticeService;
import com.fasterxml.jackson.core.type.TypeReference;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Locale;

/**
 * Publisher-side notice service.
 *
 * <p>No notice is stored in PostgreSQL any more. Create, read, update and delete
 * requests are serialised into Kafka messages and answered by the discussion
 * module. If the broker is not reachable, the same operation is repeated over
 * REST on port 24130, so a broker outage does not break the external API.</p>
 */
@Service
public class NoticeServiceImpl implements NoticeService {

    private static final Logger log = LoggerFactory.getLogger(NoticeServiceImpl.class);

    private static final String CACHE_PREFIX = "distcomp:notice:";

    private final IssueRepository issueRepository;

    private final NoticeKafkaGateway kafkaGateway;

    private final DiscussionNoticeClient restClient;

    private final CacheService cache;

    private final boolean kafkaEnabled;

    private final boolean discussionEnabled;

    public NoticeServiceImpl(IssueRepository issueRepository,
                             NoticeKafkaGateway kafkaGateway,
                             DiscussionNoticeClient restClient,
                             CacheService cache,
                             @Value("${app.kafka.enabled:true}") boolean kafkaEnabled,
                             @Value("${app.discussion.enabled:true}") boolean discussionEnabled) {
        this.issueRepository = issueRepository;
        this.kafkaGateway = kafkaGateway;
        this.restClient = restClient;
        this.cache = cache;
        this.kafkaEnabled = kafkaEnabled;
        this.discussionEnabled = discussionEnabled;
    }

    @Override
    public NoticeResponseTo create(NoticeRequestTo request) {
        validate(request);
        requireIssue(request.issueId());
        NoticeKafkaResponse response = exchange(NoticeKafkaRequest.create(
                request.issueId(), request.content()));
        cache.evictByPrefix(CACHE_PREFIX);
        return toResponse(requireNotice(response));
    }

    @Override
    public NoticeResponseTo findById(Long id) {
        if (id == null) {
            throw new ValidationException("id", "must be specified");
        }
        String key = singleKey(id);
        NoticeResponseTo cached = cache.get(key, new TypeReference<NoticeResponseTo>() {
        });
        if (cached != null) {
            return cached;
        }
        NoticeResponseTo response = toResponse(requireNotice(
                exchange(NoticeKafkaRequest.get(id))));
        cache.put(key, response);
        return response;
    }

    @Override
    public PagedResult<NoticeResponseTo> findAll(NoticeFilter filter, Pageable pageable) {
        String key = listKey(filter, pageable);
        PagedResult<NoticeResponseTo> cached = cache.get(key,
                new TypeReference<PagedResult<NoticeResponseTo>>() {
                });
        if (cached != null) {
            return cached;
        }

        Long issueId = filter == null ? null : filter.issueId();
        NoticeKafkaResponse response = exchange(NoticeKafkaRequest.findAll(
                issueId,
                pageable.isPaged() ? pageable.getPageNumber() : null,
                pageable.isPaged() ? pageable.getPageSize() : null,
                sortExpression(pageable)));

        List<NoticeResponseTo> notices = response.notices() == null
                ? List.of()
                : response.notices().stream().map(this::toResponse).toList();
        long total = response.total() == null ? notices.size() : response.total();
        PagedResult<NoticeResponseTo> result = new PagedResult<>(notices, total);
        cache.put(key, result);
        return result;
    }

    @Override
    public NoticeResponseTo update(Long id, NoticeRequestTo request) {
        validate(request);
        requireIssue(request.issueId());
        NoticeKafkaResponse response = exchange(NoticeKafkaRequest.update(
                id, request.issueId(), request.content()));
        cache.evictByPrefix(CACHE_PREFIX);
        return toResponse(requireNotice(response));
    }

    @Override
    public void delete(Long id) {
        exchange(NoticeKafkaRequest.delete(id));
        cache.evictByPrefix(CACHE_PREFIX);
    }

    @Override
    public List<NoticeResponseTo> findByIssueId(Long issueId) {
        if (issueId == null) {
            throw new ValidationException("issueId", "must be specified");
        }
        return findAll(new NoticeFilter(issueId), Pageable.unpaged()).content();
    }

    @Override
    public void deleteByIssueId(Long issueId) {
        if (issueId == null) {
            throw new ValidationException("issueId", "must be specified");
        }
        if (!discussionEnabled) {
            return;
        }
        exchange(NoticeKafkaRequest.deleteByIssue(issueId));
        cache.evictByPrefix(CACHE_PREFIX);
    }

    private NoticeKafkaResponse exchange(NoticeKafkaRequest request) {
        if (!discussionEnabled) {
            throw new ApiException(HttpStatus.SERVICE_UNAVAILABLE, "50310",
                    "Discussion module is disabled by configuration");
        }
        if (kafkaEnabled) {
            try {
                return ensureSuccess(kafkaGateway.request(request));
            } catch (KafkaTransportException exception) {
                log.warn("Kafka is unavailable, falling back to REST: {}", exception.getMessage());
            }
        }
        return restExchange(request);
    }

    private NoticeKafkaResponse restExchange(NoticeKafkaRequest request) {
        return switch (request.operation()) {
            case CREATE -> NoticeKafkaResponse.ok(restClient.create(
                    new NoticeRequestTo(null, request.issueId(), request.content(), null)));
            case GET -> NoticeKafkaResponse.ok(restClient.findById(request.id()));
            case FIND_ALL -> {
                PagedResult<NoticeDto> page = restClient.findAll(request.issueId(),
                        request.page(), request.size(), request.sort());
                yield NoticeKafkaResponse.ok(page.content(), page.total());
            }
            case UPDATE -> NoticeKafkaResponse.ok(restClient.update(request.id(),
                    new NoticeRequestTo(request.id(), request.issueId(), request.content(), null)));
            case DELETE -> {
                restClient.delete(request.id());
                yield NoticeKafkaResponse.empty();
            }
            case DELETE_BY_ISSUE -> {
                restClient.deleteByIssueId(request.issueId());
                yield NoticeKafkaResponse.empty();
            }
        };
    }

    private NoticeKafkaResponse ensureSuccess(NoticeKafkaResponse response) {
        if (!response.success()) {
            throw toException(response);
        }
        return response;
    }

    private ApiException toException(NoticeKafkaResponse response) {
        String code = response.errorCode() == null ? "50302" : response.errorCode();
        HttpStatus status;
        try {
            status = HttpStatus.valueOf(Integer.parseInt(code.substring(0, 3)));
        } catch (RuntimeException exception) {
            status = HttpStatus.SERVICE_UNAVAILABLE;
        }
        return new RemoteDiscussionException(status, code, response.errorMessage());
    }

    private NoticeDto requireNotice(NoticeKafkaResponse response) {
        if (response.notice() == null) {
            throw new RemoteDiscussionException(HttpStatus.BAD_GATEWAY, "50201",
                    "Discussion module returned an empty notice");
        }
        return response.notice();
    }

    private void requireIssue(Long issueId) {
        if (issueId == null) {
            throw new ValidationException("issueId", "must be specified");
        }
        if (!issueRepository.existsById(issueId)) {
            throw new EntityNotFoundException("Issue", issueId);
        }
    }

    private void validate(NoticeRequestTo request) {
        if (request == null) {
            throw new ValidationException("Request body must be specified");
        }
        if (request.issueId() == null) {
            throw new ValidationException("issueId", "must be specified");
        }
        if (request.content() == null || request.content().isBlank()) {
            throw new ValidationException("content", "must be specified");
        }
        if (request.content().length() < 2 || request.content().length() > 2048) {
            throw new ValidationException("content", "length must be between 2 and 2048 characters");
        }
    }

    private NoticeResponseTo toResponse(NoticeDto notice) {
        return new NoticeResponseTo(notice.id(), notice.issueId(), notice.content(), notice.state());
    }

    private String singleKey(Long id) {
        return CACHE_PREFIX + id;
    }

    private String listKey(NoticeFilter filter, Pageable pageable) {
        Long issueId = filter == null ? null : filter.issueId();
        String paging = pageable.isPaged()
                ? pageable.getPageNumber() + ":" + pageable.getPageSize()
                : "all";
        String sort = pageable.getSort().isSorted() ? pageable.getSort().toString() : "id: ASC";
        return CACHE_PREFIX + "list:" + issueId + ":" + paging + ":" + sort;
    }

    /**
     * The discussion module accepts the compact {@code property,direction} form,
     * while {@link org.springframework.data.domain.Sort#toString()} produces
     * {@code property: DIRECTION}. Only the first order is forwarded because the
     * remote comparator supports a single property.
     */
    private String sortExpression(Pageable pageable) {
        if (!pageable.getSort().isSorted()) {
            return "id,asc";
        }
        return pageable.getSort().stream()
                .findFirst()
                .map(order -> order.getProperty() + ","
                        + order.getDirection().name().toLowerCase(Locale.ROOT))
                .orElse("id,asc");
    }
}
