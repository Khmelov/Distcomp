package com.distcomp.common.kafka;

import java.util.UUID;

/**
 * Request envelope of the publisher-to-discussion exchange.
 *
 * @param requestId correlates the request with its reply
 * @param operation operation to execute
 * @param id        notice identifier for point operations
 * @param issueId   issue identifier, also used as the partitioning key
 * @param content   notice text for create and update
 * @param page      zero-based page number for list requests
 * @param size      page size for list requests
 * @param sort      sort expression, for example {@code id,asc}
 */
public record NoticeKafkaRequest(
        String requestId,
        NoticeOperation operation,
        Long id,
        Long issueId,
        String content,
        Integer page,
        Integer size,
        String sort) {

    public static NoticeKafkaRequest create(Long issueId, String content) {
        return new NoticeKafkaRequest(UUID.randomUUID().toString(), NoticeOperation.CREATE,
                null, issueId, content, null, null, null);
    }

    public static NoticeKafkaRequest get(Long id) {
        return new NoticeKafkaRequest(UUID.randomUUID().toString(), NoticeOperation.GET,
                id, null, null, null, null, null);
    }

    public static NoticeKafkaRequest findAll(Long issueId, Integer page, Integer size, String sort) {
        return new NoticeKafkaRequest(UUID.randomUUID().toString(), NoticeOperation.FIND_ALL,
                null, issueId, null, page, size, sort);
    }

    public static NoticeKafkaRequest update(Long id, Long issueId, String content) {
        return new NoticeKafkaRequest(UUID.randomUUID().toString(), NoticeOperation.UPDATE,
                id, issueId, content, null, null, null);
    }

    public static NoticeKafkaRequest delete(Long id) {
        return new NoticeKafkaRequest(UUID.randomUUID().toString(), NoticeOperation.DELETE,
                id, null, null, null, null, null);
    }

    public static NoticeKafkaRequest deleteByIssue(Long issueId) {
        return new NoticeKafkaRequest(UUID.randomUUID().toString(), NoticeOperation.DELETE_BY_ISSUE,
                null, issueId, null, null, null, null);
    }

    /** @return the Kafka key that keeps every message of one issue in one partition */
    public String partitionKey() {
        if (issueId != null) {
            return "issue-" + issueId;
        }
        if (id != null) {
            return "notice-" + id;
        }
        return "notice-unknown";
    }
}
