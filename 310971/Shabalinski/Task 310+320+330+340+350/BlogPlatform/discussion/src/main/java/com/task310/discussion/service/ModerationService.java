package com.task310.discussion.service;

import com.task310.discussion.model.PostState;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.List;

@Service
public class ModerationService {

    private static final List<String> STOP_WORDS = Arrays.asList(
        "spam", "advertisement", "promo", "buy now", "click here"
    );

    public PostState moderate(String content) {
        if (content == null || content.trim().isEmpty()) {
            return PostState.DECLINE;
        }

        String lowerContent = content.toLowerCase();
        
        // Check for stop words
        for (String stopWord : STOP_WORDS) {
            if (lowerContent.contains(stopWord)) {
                return PostState.DECLINE;
            }
        }

        // Simple length check
        if (content.trim().length() < 2) {
            return PostState.DECLINE;
        }

        // If passed all checks, approve
        return PostState.APPROVE;
    }
}

