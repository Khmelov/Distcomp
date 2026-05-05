package by.bsuir.task361.discussion.service;

import by.bsuir.task361.discussion.dto.ReactionState;
import org.springframework.stereotype.Service;

import java.util.Locale;
import java.util.Set;

@Service
public class ReactionModerationService {
    private static final Set<String> STOP_WORDS = Set.of(
            "spam", "scam", "fake", "hate", "abuse", "badword",
            "спам", "скам", "фейк", "ненависть", "оскорбление", "запрещено"
    );

    public ReactionState moderate(String content) {
        String normalized = content == null ? "" : content.toLowerCase(Locale.ROOT);
        return STOP_WORDS.stream().anyMatch(normalized::contains)
                ? ReactionState.DECLINE
                : ReactionState.APPROVE;
    }
}
