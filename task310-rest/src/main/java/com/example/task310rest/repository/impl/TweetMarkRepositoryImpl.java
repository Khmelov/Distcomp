package com.example.task310rest.repository.impl;

import com.example.task310rest.entity.TweetMark;
import com.example.task310rest.repository.TweetMarkRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

/**
 * InMemory реализация репозитория для связи Tweet-Mark
 */
@Repository
public class TweetMarkRepositoryImpl implements TweetMarkRepository {
    
    /**
     * Хранилище связей Tweet-Mark
     * Ключ: "tweetId:markId"
     */
    private final Set<String> storage = ConcurrentHashMap.newKeySet();
    
    private String createKey(Long tweetId, Long markId) {
        return tweetId + ":" + markId;
    }
    
    @Override
    public void addTweetMark(Long tweetId, Long markId) {
        storage.add(createKey(tweetId, markId));
    }
    
    @Override
    public boolean removeTweetMark(Long tweetId, Long markId) {
        return storage.remove(createKey(tweetId, markId));
    }
    
    @Override
    public List<Long> getMarkIdsByTweetId(Long tweetId) {
        return storage.stream()
                .filter(key -> key.startsWith(tweetId + ":"))
                .map(key -> Long.parseLong(key.split(":")[1]))
                .collect(Collectors.toList());
    }
    
    @Override
    public List<Long> getTweetIdsByMarkId(Long markId) {
        String markIdStr = ":" + markId;
        return storage.stream()
                .filter(key -> key.endsWith(markIdStr))
                .map(key -> Long.parseLong(key.split(":")[0]))
                .collect(Collectors.toList());
    }
    
    @Override
    public List<Long> getTweetIdsByMarkName(String markName) {
        // Этот метод требует дополнительного контекста (MarkRepository)
        // Реализация будет в сервисном слое
        throw new UnsupportedOperationException("Use service layer for this operation");
    }
    
    @Override
    public void removeAllByTweetId(Long tweetId) {
        storage.removeIf(key -> key.startsWith(tweetId + ":"));
    }
    
    @Override
    public void removeAllByMarkId(Long markId) {
        String markIdStr = ":" + markId;
        storage.removeIf(key -> key.endsWith(markIdStr));
    }
}
