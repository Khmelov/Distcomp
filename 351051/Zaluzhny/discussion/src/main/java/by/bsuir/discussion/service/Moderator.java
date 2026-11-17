package by.bsuir.discussion.service;

import by.bsuir.discussion.dto.MessageState;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Locale;

@Component
public class Moderator {

    static final List<String> STOP_WORDS = List.of("spam", "hate", "abuse");

    public MessageState decide(String content) {
        if (content == null) {
            return MessageState.APPROVE;
        }
        String normalized = content.toLowerCase(Locale.ROOT);
        for (String word : STOP_WORDS) {
            if (normalized.contains(word)) {
                return MessageState.DECLINE;
            }
        }
        return MessageState.APPROVE;
    }
}
