package com.task.discussion.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.List;

@Slf4j
@Service
public class ModerationService {

    private static final List<String> STOP_WORDS = Arrays.asList(
            "spam", "advertisement", "banned", "prohibited",
            "illegal", "hate", "violence", "abuse"
    );

    public String moderate(String content) {
        if (content == null || content.isEmpty()) {
            return "APPROVE";
        }

        String lowerContent = content.toLowerCase();

        for (String stopWord : STOP_WORDS) {
            if (lowerContent.contains(stopWord)) {
                log.info("Content contains stop word '{}' - DECLINE", stopWord);
                return "DECLINE";
            }
        }

        log.info("Content passed moderation - APPROVE");
        return "APPROVE";
    }
}