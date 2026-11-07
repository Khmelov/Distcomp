package com.distcomp.discussion.service;

import com.distcomp.common.notice.NoticeState;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Locale;

/**
 * Automatic pre-moderation of notices.
 *
 * <p>The rule is intentionally simple and deterministic: a notice containing a
 * stop word is declined, everything else is approved. The list can be extended
 * without touching the persistence or transport layers.</p>
 */
@Service
public class ModerationService {

    private static final List<String> STOP_WORDS = List.of(
            "spam", "scam", "hate", "abuse", "badword",
            "спам", "реклама", "мат", "дурак");

    public NoticeState moderate(String content) {
        if (content == null || content.isBlank()) {
            return NoticeState.DECLINE;
        }
        String normalized = content.toLowerCase(Locale.ROOT);
        for (String stopWord : STOP_WORDS) {
            if (normalized.contains(stopWord)) {
                return NoticeState.DECLINE;
            }
        }
        return NoticeState.APPROVE;
    }
}
