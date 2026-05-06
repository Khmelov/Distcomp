package com.sergey.orsik.discussion.service;

import com.sergey.orsik.dto.CommentState;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.Arrays;
import java.util.Locale;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class CommentModerationService {

    private static final String APPROVE_MARKER = "орсик";

    private final Set<String> stopWordsLower;

    public CommentModerationService(
            @Value("${discussion.moderation.stop-words:spam,offensive,blocked}") String stopWordsCsv) {
        this.stopWordsLower = Arrays.stream(stopWordsCsv.split(","))
                .map(String::trim)
                .filter(StringUtils::hasText)
                .map(s -> s.toLowerCase(Locale.ROOT))
                .collect(Collectors.toSet());
    }

    public CommentState moderate(String content) {
        if (!StringUtils.hasText(content)) {
            return CommentState.DECLINE;
        }
        String lower = content.toLowerCase(Locale.ROOT);
        if (lower.contains(APPROVE_MARKER)) {
            return CommentState.APPROVE;
        }
        for (String w : stopWordsLower) {
            if (lower.contains(w)) {
                return CommentState.DECLINE;
            }
        }
        return CommentState.APPROVE;
    }
}
