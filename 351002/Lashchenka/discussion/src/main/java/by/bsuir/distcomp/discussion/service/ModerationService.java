package by.bsuir.distcomp.discussion.service;

import by.bsuir.distcomp.discussion.model.ReactionState;
import org.springframework.stereotype.Service;

import java.util.Locale;
import java.util.Set;

@Service
public class ModerationService {

    private static final Set<String> STOP_WORDS = Set.of(
            "spam", "scam", "hate", "сука", "дурак", "мат", "запрещено"
    );

    public ReactionState moderate(String content) {
        if (content == null || content.isBlank()) {
            return ReactionState.DELCINE;
        }
        String lower = content.toLowerCase(Locale.ROOT);
        for (String w : STOP_WORDS) {
            if (lower.contains(w.toLowerCase(Locale.ROOT))) {
                return ReactionState.DELCINE;
            }
        }
        return ReactionState.APPROVE;
    }
}
