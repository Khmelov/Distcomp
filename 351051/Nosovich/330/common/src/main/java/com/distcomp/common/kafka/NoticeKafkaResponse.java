package com.distcomp.common.kafka;

import com.distcomp.common.notice.NoticeDto;

import java.util.List;

/**
 * Reply envelope of the discussion-to-publisher exchange.
 *
 * @param requestId    identifier copied from the request
 * @param success      {@code true} when the operation completed
 * @param errorCode    five-digit application code of a failure
 * @param errorMessage human readable explanation of a failure
 * @param notice       single notice for point operations
 * @param notices      notice collection for list operations
 * @param total        total number of notices matching a list request
 */
public record NoticeKafkaResponse(
        String requestId,
        boolean success,
        String errorCode,
        String errorMessage,
        NoticeDto notice,
        List<NoticeDto> notices,
        Long total) {

    public static NoticeKafkaResponse ok(NoticeDto notice) {
        return new NoticeKafkaResponse(null, true, null, null, notice, null, null);
    }

    public static NoticeKafkaResponse ok(List<NoticeDto> notices, long total) {
        return new NoticeKafkaResponse(null, true, null, null, null, notices, total);
    }

    public static NoticeKafkaResponse empty() {
        return new NoticeKafkaResponse(null, true, null, null, null, null, 0L);
    }

    public static NoticeKafkaResponse error(String errorCode, String errorMessage) {
        return new NoticeKafkaResponse(null, false, errorCode, errorMessage, null, null, null);
    }

    public NoticeKafkaResponse withRequestId(String value) {
        return new NoticeKafkaResponse(value, success, errorCode, errorMessage, notice, notices, total);
    }
}
