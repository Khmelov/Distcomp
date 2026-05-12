package org.rv.lab1.discussion.service;

import org.rv.lab1.discussion.domain.CommentState;
import org.springframework.stereotype.Service;

import java.util.Locale;
import java.util.Set;

/**
 * Simple automatic moderation: decline if content contains a stop-word.
 * Updates comment state to APPROVE or DECLINE as per assignment.
 */
@Service
public class CommentModerationService {
    private static final Set<String> STOP_WORDS = Set.of(
            "spam", "offensive", "hate", "forbidden"
    );

    public CommentState moderate(String content) {
        if (content == null || content.isBlank()) {
            return CommentState.DECLINE;
        }
        String t = content.toLowerCase(Locale.ROOT);
        for (String w : STOP_WORDS) {
            if (t.contains(w)) {
                return CommentState.DECLINE;
            }
        }
        return CommentState.APPROVE;
    }
}
