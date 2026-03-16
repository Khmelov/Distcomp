package com.example.restApi.repository;

import com.example.restApi.model.Issue;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public class IssueRepository extends InMemoryRepository<Issue> {

    public List<Issue> findByEditorId(Long editorId) {
        return storage.values().stream()
                .filter(i -> i.getEditorId().equals(editorId))
                .toList();
    }

    public List<Issue> findByMarkerId(Long markerId) {
        return storage.values().stream()
                .filter(i -> i.getMarkerIds() != null && i.getMarkerIds().contains(markerId))
                .toList();
    }

    public boolean existsByTitle(String title) {
        return storage.values().stream()
                .anyMatch(i -> i.getTitle().equals(title));
    }

    public boolean existsByTitleAndIdNot(String title, Long id) {
        return storage.values().stream()
                .anyMatch(i -> i.getTitle().equals(title) && !i.getId().equals(id));
    }
}
