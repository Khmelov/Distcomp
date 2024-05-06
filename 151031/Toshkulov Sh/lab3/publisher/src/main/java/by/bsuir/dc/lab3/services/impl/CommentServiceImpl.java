package by.bsuir.dc.lab3.services.impl;

import by.bsuir.dc.lab3.entities.Comment;
import by.bsuir.dc.lab3.entities.News;
import by.bsuir.dc.lab3.services.interfaces.CommentService;
import by.bsuir.dc.lab3.services.repos.NewsRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;
import reactor.core.publisher.Flux;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class CommentServiceImpl implements CommentService {


    private WebClient repos = WebClient.create();

    @Autowired
    private NewsRepository newsRepo;

    @Override
    public Comment add(Comment comment) {
        Optional<News> relatedNews = newsRepo.findById(comment.getNewsId());
        if(relatedNews.isPresent()){
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            try {
                Optional<Comment> savedComment = repos.post()
                        .uri("http://localhost:24130/api/v1.0/comments")
                        .headers(httpHeaders -> httpHeaders.addAll(headers))
                        .bodyValue(comment)
                        .retrieve()
                        .bodyToMono(Comment.class)
                        .blockOptional();
                return savedComment.get();
            }catch (WebClientResponseException e){
                return null;
            }
        }else {
            return null;
        }

    }

    @Override
    public void delete(long id) {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        try {
            Optional<Comment> comment = repos.get()
                    .uri("http://localhost:24130/api/v1.0/comments/" + id)
                    .headers(httpHeaders -> httpHeaders.addAll(headers))
                    .retrieve()
                    .bodyToMono(Comment.class)
                    .blockOptional();
            if (comment.isPresent()) {
                headers.setContentType(MediaType.APPLICATION_JSON);
                repos.delete()
                        .uri("http://localhost:24130/api/v1.0/comments/" + id)
                        .headers(httpHeaders -> httpHeaders.addAll(headers))
                        .retrieve()
                        .bodyToMono(Comment.class)
                        .block();
            }
        }catch (WebClientResponseException e){
            return;
        }
    }

    @Override
    public Comment update(Comment comment) {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        Optional<News> relatedNews = newsRepo.findById(comment.getNewsId());
        if(relatedNews.isPresent()) {
            try {
                Optional<Comment> updatedComment = repos.put()
                        .uri("http://localhost:24130/api/v1.0/comments")
                        .headers(httpHeaders -> httpHeaders.addAll(headers))
                        .bodyValue(comment)
                        .retrieve()
                        .bodyToMono(Comment.class)
                        .blockOptional();
                return updatedComment.get();
            }catch (WebClientResponseException e){
                e.printStackTrace();
                return null;
            }
        }else {
            return null;
        }
    }

    @Override
    public Comment getById(long id) {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        try {
            Optional<Comment> comment = repos.get()
                    .uri("http://localhost:24130/api/v1.0/comments/" + id)
                    .headers(httpHeaders -> httpHeaders.addAll(headers))
                    .retrieve()
                    .bodyToMono(Comment.class)
                    .blockOptional();
            if (comment.isPresent()) {
                return comment.get();
            } else {
                return null;
            }
        }catch (WebClientResponseException e){
            return null;
        }
    }

    @Override
    public List<Comment> getAll() {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        try {
            List<Comment> comments = repos.get()
                    .uri("http://localhost:24130/api/v1.0/comments")
                    .headers(httpHeaders -> httpHeaders.addAll(headers))
                    .retrieve()
                    .bodyToFlux(Comment.class)
                    .collectList()
                    .block();
            return comments;
        }catch (WebClientResponseException e){
            return new ArrayList<>();
        }
    }
}
