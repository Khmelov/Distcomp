package com.task.rest.repository;

import com.task.rest.model.Notice;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.stream.Collectors;

@Repository
public class NoticeRepository extends InMemoryRepository<Notice> {

    @Override
    protected Long getId(Notice entity) {
        return entity.getId();
    }

    @Override
    protected void setId(Notice entity, Long id) {
        entity.setId(id);
    }

    public List<Notice> findByTweetId(Long tweetId) {
        return storage.values().stream()
                .filter(notice -> notice.getTweetId().equals(tweetId))
                .collect(Collectors.toList());
    }
}