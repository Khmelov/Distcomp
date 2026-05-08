package com.messageservice.services;

import com.messageservice.models.MessageState;
import org.springframework.stereotype.Service;

import java.util.Set;

@Service
public class MessageModerationService {

    private static final Set<String> STOP_WORDS = Set.of("spam", "scam", "hate", "offensive");

    public MessageState moderate(String content) {
        String normalized = content == null ? "" : content.toLowerCase();
        boolean declined = STOP_WORDS.stream().anyMatch(normalized::contains);
        return declined ? MessageState.DELCINE : MessageState.APPROVE;
    }
}
