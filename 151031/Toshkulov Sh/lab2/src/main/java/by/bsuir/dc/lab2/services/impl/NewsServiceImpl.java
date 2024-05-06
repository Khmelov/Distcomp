package by.bsuir.dc.lab2.services.impl;

import by.bsuir.dc.lab2.entities.Editor;
import by.bsuir.dc.lab2.entities.News;
import by.bsuir.dc.lab2.services.interfaces.NewsService;
import by.bsuir.dc.lab2.services.repos.EditorRepository;
import by.bsuir.dc.lab2.services.repos.NewsRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class NewsServiceImpl implements NewsService {
    @Autowired
    private NewsRepository repos;
    @Autowired
    private EditorRepository editorRepo;

    @Override
    public News add(News news) {
        Optional<Editor> relatedEditor = editorRepo.findById(news.getEditorId());
        if(relatedEditor.isPresent()){
            return repos.save(news);
        }else {
            return null;
        }

    }

    @Override
    public void delete(long id) {
        Optional<News> news = repos.findById(id);
        if(news.isPresent()) {
            repos.delete(news.get());
        }
    }

    @Override
    public News update(News news) {
        Optional<Editor> relatedEditor = editorRepo.findById(news.getEditorId());
        if(relatedEditor.isPresent()){
            return repos.saveAndFlush(news);
        }else {
            return null;
        }
    }

    @Override
    public News getById(long id) {
        Optional<News> news = repos.findById(id);
        if(news.isPresent()) {
            return news.get();
        } else {
            return null;
        }
    }

    @Override
    public List<News> getAll() {
        return repos.findAll();
    }

    @Override
    public News getByTitle(String title) {
        return repos.findByTitle(title);
    }
}
