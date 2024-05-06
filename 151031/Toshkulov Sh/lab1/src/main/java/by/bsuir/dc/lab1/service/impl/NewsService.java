package by.bsuir.dc.lab1.service.impl;

import by.bsuir.dc.lab1.dto.*;
import by.bsuir.dc.lab1.dto.mappers.NewsMapper;
import by.bsuir.dc.lab1.entities.News;
import by.bsuir.dc.lab1.inmemory.NewsTable;
import by.bsuir.dc.lab1.service.abst.INewsService;
import org.springframework.stereotype.Component;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;

@Component
public class NewsService implements INewsService {

    @Override
    public NewsResponseTo create(NewsRequestTo newsTo) {
        News news = NewsMapper.instance.convertFromDTO(newsTo);
        news = NewsTable.getInstance().add(news);
        if(news != null){
            return NewsMapper.instance.convertToDTO(news);
        } else {
            return null;
        }
    }

    @Override
    public NewsResponseTo getById(BigInteger id) {
        News news = NewsTable.getInstance().getById(id);
        if(news != null){
            return NewsMapper.instance.convertToDTO(news);
        } else {
            return null;
        }
    }

    @Override
    public List<NewsResponseTo> getAll() {
        List<News> news = NewsTable.getInstance().getAll();
        List<NewsResponseTo> newsTo = new ArrayList<>();
        for(News currentNews : news){
            newsTo.add(NewsMapper.instance.convertToDTO(currentNews));
        }
        return newsTo;
    }

    @Override
    public NewsResponseTo update(NewsRequestTo newsTo) {
        News updatedNews = NewsMapper.instance.convertFromDTO(newsTo);
        News news = NewsTable.getInstance().update(updatedNews);
        if(news != null){
            return NewsMapper.instance.convertToDTO(news);
        } else {
            return null;
        }
    }

    @Override
    public boolean delete(BigInteger id) {
        return NewsTable.getInstance().delete(id);
    }
}
