package by.liza.discussion.service;

import org.springframework.stereotype.Service;
import java.util.Set;

/**
 * Простая модерация на основе стоп-слов.
 * APPROVE если стоп-слов нет, DECLINE иначе.
 */
@Service
public class ModerationService {
    private static final Set<String> STOP_WORDS = Set.of(
            "spam", "hate", "abuse", "violence", "forbidden"
    );

    public String moderate(String content) {
        if (content == null) return "DECLINE";
        String lower = content.toLowerCase();
        for (String word : STOP_WORDS) {
            if (lower.contains(word)) return "DECLINE";
        }
        return "APPROVE";
    }
}