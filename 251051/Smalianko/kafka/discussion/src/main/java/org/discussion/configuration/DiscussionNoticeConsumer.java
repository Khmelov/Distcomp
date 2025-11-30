package org.discussion.configuration;

import com.common.NoticeAsyncResponse;
import com.common.NoticeMessage;
import lombok.RequiredArgsConstructor;
import org.discussion.service.NoticeService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.function.Function;

@Configuration
@RequiredArgsConstructor
public class DiscussionNoticeConsumer {

    private final NoticeService noticeService;

    @Bean
    public Function<NoticeMessage, NoticeAsyncResponse> moderateNotice() {
        return (message) -> {
            switch (message.getOperation()) {
                case CREATE -> noticeService.create(message);
                case UPDATE ->{ return noticeService.update(message); }
                case DELETE -> noticeService.delete(message.getId());
                case GET -> { return noticeService.getById(message.getId(), message.getCorrelationId()); }
                case GET_ALL -> { return noticeService.getAll(message.getCorrelationId()); }
                default -> throw new IllegalArgumentException("Unknown operation: " + message.getOperation());
            }
            return null;
        };
    }
}