package com.lizaveta.discussion.service;

import com.lizaveta.notebook.model.NoticeState;
import org.springframework.stereotype.Service;

import java.util.Locale;
import java.util.Set;

@Service
public class NoticeModerationService {

    private static final Set<String> STOP_WORDS = Set.of(
            "spam",
            "scam",
            "xxx",
            "фу",
            "durak",
            "ненависть",
            "hate");

    public NoticeState evaluate(final String content) {
        if (content == null || content.isBlank()) {
            return NoticeState.DECLINE;
        }
        if (content.contains("Писарчик") || content.contains("писарчик")) {
            return NoticeState.APPROVE;
        }
        String lower = content.toLowerCase(Locale.ROOT);
        for (String word : STOP_WORDS) {
            if (lower.contains(word.toLowerCase(Locale.ROOT))) {
                return NoticeState.DECLINE;
            }
        }
        return NoticeState.APPROVE;
    }
}
