package com.example.task310.repo;

import com.example.task310.domain.Notice;
import org.springframework.stereotype.Repository;

@Repository
public class NoticeRepo extends InMemoryRepo<Notice> {
    @Override
    protected Notice withId(Notice n, long id) {
        return new Notice(id, n.newsId(), n.content());
    }
}
