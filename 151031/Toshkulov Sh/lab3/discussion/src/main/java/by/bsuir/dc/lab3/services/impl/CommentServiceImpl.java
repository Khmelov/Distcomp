package by.bsuir.dc.lab3.services.impl;

import by.bsuir.dc.lab3.entities.Comment;
import by.bsuir.dc.lab3.entities.News;
import by.bsuir.dc.lab3.services.interfaces.CommentDiscService;
import by.bsuir.dc.lab3.services.repos.Comment2Repository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;

import java.util.List;
import java.util.Optional;

@Service
public class CommentServiceImpl implements CommentDiscService {

    @Autowired
    private Comment2Repository repos;

    private WebClient newsRepo = WebClient.create();

    @Override
    public Comment add(Comment comment) {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        try {
            Optional<News> relatedNews = newsRepo.get()
                    .uri("http://localhost:24110/api/v1.0/news/" + comment.getNewsId())
                    .headers(httpHeaders -> httpHeaders.addAll(headers))
                    .retrieve()
                    .bodyToMono(News.class)
                    .blockOptional();
            if (relatedNews.isPresent()) {
                return repos.save(comment);
            } else {
                return null;
            }
        }catch (WebClientResponseException e){
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
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        try {
            Optional<News> relatedNews = newsRepo.get()
                    .uri("http://localhost:24110/api/v1.0/news/" + comment.getNewsId())
                    .headers(httpHeaders -> httpHeaders.addAll(headers))
                    .retrieve()
                    .bodyToMono(News.class)
                    .blockOptional();
            if(relatedNews.isPresent()) {
                return repos.save(comment);
            }else {
                return null;
            }
        }catch (WebClientResponseException e){
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
