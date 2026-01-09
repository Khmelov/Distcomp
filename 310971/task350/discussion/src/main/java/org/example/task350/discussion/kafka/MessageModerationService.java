package org.example.task350.discussion.kafka;

import org.example.task350.discussion.model.MessageState;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

@Service
public class MessageModerationService {

    private static final Set<String> STOP_WORDS = new HashSet<>(Arrays.asList(
        "spam", "scam", "fraud", "hack", "virus", "malware", "phishing"
    ));

    public MessageState moderate(String content) {
        if (content == null || content.isEmpty()) {
            return MessageState.DECLINE;
        }

        String lowerContent = content.toLowerCase();
        
        // Check for stop words
        for (String stopWord : STOP_WORDS) {
            if (lowerContent.contains(stopWord)) {
                return MessageState.DECLINE;
            }
        }

        // Simple moderation: check length and basic content
        if (content.length() < 2) {
            return MessageState.DECLINE;
        }

        // If passed moderation, approve
        return MessageState.APPROVE;
    }
}

