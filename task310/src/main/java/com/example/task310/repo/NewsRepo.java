package com.example.task310.repo;

import com.example.task310.domain.News;
import org.springframework.stereotype.Repository;

@Repository
public class NewsRepo extends InMemoryRepo<News> {
    @Override
    protected News withId(News n, long id) {
        return new News(id, n.writerId(), n.title(), n.content(), n.created(), n.modified());
    }
}
