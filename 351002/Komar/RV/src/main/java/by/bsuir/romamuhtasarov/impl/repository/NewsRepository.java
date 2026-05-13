package by.bsuir.romamuhtasarov.impl.repository;

import by.bsuir.romamuhtasarov.api.InMemoryRepository;
import by.bsuir.romamuhtasarov.impl.bean.News;
import org.springframework.stereotype.Component;


import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Component
public class NewsRepository implements InMemoryRepository<News> {


    private final Map<Long, News> NewsMemory = new HashMap<>();

    @Override
    public News get(long id) {
        News News = NewsMemory.get(id);
        if (News != null) {
            News.setId(id);
        }
        return News;
    }

    @Override
    public List<News> getAll() {
        List<News> NewsList = new ArrayList<>();
        for (Long key : NewsMemory.keySet()) {
            News News = NewsMemory.get(key);
            ;
            News.setId(key);
            NewsList.add(News);
        }
        return NewsList;
    }

    @Override
    public News delete(long id) {
        return NewsMemory.remove(id);
    }

    @Override
    public News insert(News insertObject) {
        NewsMemory.put(insertObject.getId(), insertObject);
        return insertObject;
    }

    @Override
    public boolean update(News updatingValue) {
        return NewsMemory.replace(updatingValue.getId(), NewsMemory.get(updatingValue.getId()), updatingValue);
    }

}