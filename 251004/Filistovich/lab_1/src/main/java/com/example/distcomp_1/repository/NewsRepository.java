package com.example.distcomp_1.repository;

import com.example.distcomp_1.mdoel.News;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

@Repository
public class NewsRepository {
    private Long currentId = 0L;
    private final HashMap<Long, News> news = new HashMap<>();

    public News add(News news) {
        news.setId(currentId++);
        this.news.put(news.getId(), news);
        return news;
    }
    public News get(long id) {
        return news.get(id);
    }

    public List<News> getNews() {
        return new ArrayList<>(news.values());
    }
    public void delete(long id) {
        news.remove(id);
    }

    public News update(News author) {
        news.put(author.getId(), author);
        return author;
    }
}
