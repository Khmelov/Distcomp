package by.bsuir.romamuhtasarov.impl.service;

import by.bsuir.romamuhtasarov.api.Service;
import by.bsuir.romamuhtasarov.impl.bean.News;
import by.bsuir.romamuhtasarov.api.NewsMapper;
import by.bsuir.romamuhtasarov.impl.dto.NewsRequestTo;
import by.bsuir.romamuhtasarov.impl.dto.NewsResponseTo;
import by.bsuir.romamuhtasarov.impl.repository.NewsRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
public class NewsService implements Service<NewsResponseTo, NewsRequestTo> {
    @Autowired
    private NewsRepository NewsRepository;

    public NewsService() {

    }

    public List<NewsResponseTo> getAll() {
        List<News> NewsList = NewsRepository.getAll();
        List<NewsResponseTo> resultList = new ArrayList<>();
        for (int i = 0; i < NewsList.size(); i++) {
            resultList.add(NewsMapper.INSTANCE.NewsToNewsResponseTo(NewsList.get(i)));
        }
        return resultList;
    }

    public NewsResponseTo update(NewsRequestTo updatingNews) {
        News News = NewsMapper.INSTANCE.NewsRequestToToNews(updatingNews);
        if (validateNews(News)) {
            boolean result = NewsRepository.update(News);
            NewsResponseTo responseTo = result ? NewsMapper.INSTANCE.NewsToNewsResponseTo(News) : null;
            return responseTo;
        } else return new NewsResponseTo();
        //return responseTo;
    }

    public NewsResponseTo get(long id) {
        return NewsMapper.INSTANCE.NewsToNewsResponseTo(NewsRepository.get(id));
    }

    public NewsResponseTo delete(long id) {
        return NewsMapper.INSTANCE.NewsToNewsResponseTo(NewsRepository.delete(id));
    }

    public NewsResponseTo add(NewsRequestTo NewsRequestTo) {
        News News = NewsMapper.INSTANCE.NewsRequestToToNews(NewsRequestTo);
        return NewsMapper.INSTANCE.NewsToNewsResponseTo(NewsRepository.insert(News));
    }

    private boolean validateNews(News News) {
        String title = News.getTitle();
        String content = News.getContent();

        if ((content.length() >= 4 && content.length() <= 2048) && (title.length() >= 2 && title.length() <= 64)) {
            return true;
        }
        return false;
    }
}