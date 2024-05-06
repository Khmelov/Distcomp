package by.bsuir.dc.lab1.inmemory;

import by.bsuir.dc.lab1.entities.News;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;

public class NewsTable {
    private static NewsTable instance;
    private BigInteger id = BigInteger.valueOf(1);
    private List<News> news = new ArrayList<>();

    private NewsTable(){

    }

    public News add(News news){
        news.setId(id);
        this.news.add(news);
        id = id.add(BigInteger.valueOf(1));
        return news;
    }
    public boolean delete(BigInteger id){
        for(News currentNews : this.news){
            if(currentNews.getId().equals(id)){
                return news.remove(currentNews);
            }
        }
        return false;
    }
    public News getById(BigInteger id){
        for(News news : this.news){
            if(news.getId().equals(id)){
                return news;
            }
        }
        return null;
    }
    public List<News> getAll(){
        return news;
    }

    public News update(News news){
        News oldNews = getById(news.getId());
        if(oldNews != null){
            this.news.set(this.news.indexOf(oldNews), news);
            return news;
        } else {
            return null;
        }
    }

    public static NewsTable getInstance(){
        if(instance == null){
            instance = new NewsTable();
        }
        return instance;
    }
}
