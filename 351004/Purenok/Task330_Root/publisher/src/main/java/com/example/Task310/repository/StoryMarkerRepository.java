package com.example.Task310.repository;

import org.springframework.stereotype.Repository;

import java.util.Collections;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

@Repository
public class StoryMarkerRepository {


    
    // Хранит связи: Ключ - ID Story, Значение - набор ID Markers
    private final Map<Long, Set<Long>> storyToMarkers = new ConcurrentHashMap<>();

    public void addMarkerToStory(Long storyId, Long markerId) {
        storyToMarkers.computeIfAbsent(storyId, k -> new HashSet<>()).add(markerId);
    }

    public Set<Long> getMarkersByStoryId(Long storyId) {
        return storyToMarkers.getOrDefault(storyId, Collections.emptySet());
    }
}