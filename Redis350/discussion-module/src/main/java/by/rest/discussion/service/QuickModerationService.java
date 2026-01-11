package by.rest.discussion.service;

import by.rest.discussion.dto.kafka.CommentKafkaRequest;
import by.rest.discussion.dto.kafka.ModerationResult;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.List;

@Service
public class QuickModerationService {
    
    private final List<String> stopWords = Arrays.asList(
        "viagra", "casino", "gambling", "porn", "xxx", "spam",
        "реклама", "казино", "азарт", "порно", "спам"
    );
    
    public ModerationResult moderate(CommentKafkaRequest request) {
        String content = request.getContent().toLowerCase();
        
        // Проверка на стоп-слова
        for (String stopWord : stopWords) {
            if (content.contains(stopWord)) {
                return new ModerationResult(
                    request.getCommentId(),
                    request.getStoryId(),
                    "DECLINE",
                    "Contains stop word: " + stopWord
                );
            }
        }
        
        // Проверка длины
        if (content.length() < 5) {
            return new ModerationResult(
                request.getCommentId(),
                request.getStoryId(),
                "DECLINE",
                "Too short (min 5 chars)"
            );
        }
        
        // Одобряем
        return new ModerationResult(
            request.getCommentId(),
            request.getStoryId(),
            "APPROVE",
            "OK"
        );
    }
}