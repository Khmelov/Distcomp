package by.bsuir.dc.lab2.services.impl;

import by.bsuir.dc.lab2.entities.Comment;
import by.bsuir.dc.lab2.entities.News;
import by.bsuir.dc.lab2.services.interfaces.CommentService;
import by.bsuir.dc.lab2.services.repos.CommentRepository;
import by.bsuir.dc.lab2.services.repos.NewsRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class CommentServiceImpl implements CommentService {

    @Autowired
    private CommentRepository repos;

    @Autowired
    private NewsRepository newsRepo;

    @Override
    public Comment add(Comment comment) {
        Optional<News> relatedNews = newsRepo.findById(comment.getNewsId());
        if(relatedNews.isPresent()){
            return repos.save(comment);
        }else {
            return null;
        }

    }

    @Override
    public void delete(long id) {
        Optional<Comment> comment = repos.findById(id);
        if(comment.isPresent()) {
            repos.delete(comment.get());
        }
    }

    @Override
    public Comment update(Comment comment) {
        Optional<News> relatedNews = newsRepo.findById(comment.getNewsId());
        if(relatedNews.isPresent()) {
            return repos.saveAndFlush(comment);
        }else {
            return null;
        }
    }

    @Override
    public Comment getById(long id) {
        Optional<Comment> comment = repos.findById(id);
        if(comment.isPresent()) {
            return comment.get();
        } else {
            return null;
        }
    }

    @Override
    public List<Comment> getAll() {
        return repos.findAll();
    }
}
