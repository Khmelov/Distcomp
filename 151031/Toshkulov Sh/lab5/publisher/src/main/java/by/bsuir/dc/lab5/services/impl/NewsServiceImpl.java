package by.bsuir.dc.lab5.services.impl;

import by.bsuir.dc.lab5.entities.Editor;
import by.bsuir.dc.lab5.entities.News;
import by.bsuir.dc.lab5.redis.RedisNewsRepository;
import by.bsuir.dc.lab5.services.interfaces.NewsService;
import by.bsuir.dc.lab5.services.repos.EditorRepository;
import by.bsuir.dc.lab5.services.repos.NewsRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class NewsServiceImpl implements NewsService {
    @Autowired
    private NewsRepository repos;

    @Autowired
    private RedisNewsRepository redisRepos;

    @Autowired
    private EditorRepository editorRepo;

    @Override
    public News add(News news) {
        Optional<Editor> relatedEditor = editorRepo.findById(news.getEditorId());
        if(relatedEditor.isPresent()){
            News saved = repos.save(news);
            redisRepos.add(saved);
            return saved;
        }else {
            return null;
        }

    }

    @Override
    public void delete(long id) {
        Optional<News> news = repos.findById(id);
        if(news.isPresent()) {
            redisRepos.delete(id);
            repos.delete(news.get());
        }
    }

    @Override
    public News update(News news) {
        Optional<News> cachedNews = redisRepos.getById(news.getId());
        if(cachedNews.isPresent() && news.equals(cachedNews.get())){
            return news;
        }
        Optional<Editor> relatedEditor = editorRepo.findById(news.getEditorId());
        if(relatedEditor.isPresent()){
            News updatedNews = repos.saveAndFlush(news);
            redisRepos.update(updatedNews);
            return updatedNews;
        }else {
            return null;
        }
    }

    @Override
    public News getById(long id) {

        Optional<News> news = redisRepos.getById(id);

        if(news.isPresent()) {
            return news.get();
        } else {
            news = repos.findById(id);

            if(news.isPresent()) {
                redisRepos.add(news.get());
                return news.get();
            } else {
                return null;
            }
        }
    }

    @Override
    public List<News> getAll() {
        List<News> news = redisRepos.getAll();
        if(!news.isEmpty()){
            return news;
        }else{
            news = repos.findAll();
            if(!news.isEmpty()){
                for(News currentNews : news){
                    redisRepos.add(currentNews);
                }
                return news;
            }else{
                return new ArrayList<>();
            }
        }
    }

    @Override
    public News getByTitle(String title) {
        return repos.findByTitle(title);
    }
}
