package org.discussion.configuration;

import com.common.NoteAsyncResponse;
import com.common.NoteMessage;
import lombok.RequiredArgsConstructor;
import org.discussion.service.NoteService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.function.Function;

@Configuration
@RequiredArgsConstructor
public class DiscussionNoticeConsumer {

    private final NoteService noteService;

    @Bean
    public Function<NoteMessage, NoteAsyncResponse> moderateNotice() {
        return (message) -> {
            switch (message.getOperation()) {
                case CREATE -> noteService.create(message);
                case UPDATE ->{ return noteService.update(message); }
                case DELETE -> noteService.delete(message.getId());
                case GET -> { return noteService.getById(message.getId(), message.getCorrelationId()); }
                case GET_ALL -> { return noteService.getAll(message.getCorrelationId()); }
                default -> throw new IllegalArgumentException("Unknown operation: " + message.getOperation());
            }
            return null;
        };
    }
}