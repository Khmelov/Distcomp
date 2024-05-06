package by.bsuir.dc.lab4.services.impl;

import by.bsuir.dc.lab4.dto.CommentRequestTo;
import by.bsuir.dc.lab4.dto.mappers.CommentMapper;
import by.bsuir.dc.lab4.entities.Comment;
import by.bsuir.dc.lab4.entities.News;
import by.bsuir.dc.lab4.kafka.KafkaRequest;
import by.bsuir.dc.lab4.kafka.KafkaPublisherMessaging;
import by.bsuir.dc.lab4.kafka.Methods;
import by.bsuir.dc.lab4.services.interfaces.CommentService;
import by.bsuir.dc.lab4.services.repos.NewsRepository;
import com.fasterxml.jackson.core.JsonProcessingException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
public class CommentServiceImpl implements CommentService {
    @Autowired
    private KafkaPublisherMessaging sender;
    @Autowired
    private NewsRepository newsRepo;

    @Override
    public Comment add(Comment comment) {
        Optional<News> relatedNews = newsRepo.findById(comment.getNewsId());
        if(relatedNews.isPresent()){
            List<CommentRequestTo> result = new ArrayList<>();
            CommentRequestTo commentRequestTo = CommentMapper.instance.convertCommentToRequest(comment);
            KafkaRequest request= new KafkaRequest();
            request.setKey(UUID.randomUUID());
            request.getDtoToTransfer().add(commentRequestTo);
            request.setRequestMethod(Methods.CREATE);
            try {
                result = sender.sendMessage(request);
                if(result.isEmpty()){
                    return null;
                }else{
                    CommentRequestTo updatedComment = result.get(0);
                    return CommentMapper.instance.convertRequestToComment(updatedComment);
                }
            }catch (JsonProcessingException e){
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
        CommentRequestTo commentRequestTo = new CommentRequestTo();
        commentRequestTo.setId(id);
        KafkaRequest request = new KafkaRequest();
        request.setKey(UUID.randomUUID());
        request.getDtoToTransfer().add(commentRequestTo);
        request.setRequestMethod(Methods.DELETE);
        try {
            sender.sendMessage(request);
        } catch (JsonProcessingException e) {
            return;
        }

    }

    @Override
    public Comment update(Comment comment) {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        Optional<News> relatedNews = newsRepo.findById(comment.getNewsId());
        if(relatedNews.isPresent()) {
            List<CommentRequestTo> result = new ArrayList<>();
            CommentRequestTo commentRequestTo = CommentMapper.instance.convertCommentToRequest(comment);
            KafkaRequest request= new KafkaRequest();
            request.setKey(UUID.randomUUID());
            request.getDtoToTransfer().add(commentRequestTo);
            request.setRequestMethod(Methods.UPDATE);
            try {
                result = sender.sendMessage(request);
                if(result.isEmpty()){
                    return null;
                }else{
                    CommentRequestTo updatedComment = result.get(0);
                    return CommentMapper.instance.convertRequestToComment(updatedComment);
                }
            }catch (JsonProcessingException e){
                return null;
            }
        }else {
            return null;
        }
    }

    @Override
    public Comment getById(long id) {
        List<CommentRequestTo> result = new ArrayList<>();
        CommentRequestTo commentRequestTo = new CommentRequestTo();
        commentRequestTo.setId(id);
        KafkaRequest request= new KafkaRequest();
        request.setKey(UUID.randomUUID());
        request.getDtoToTransfer().add(commentRequestTo);
        request.setRequestMethod(Methods.GET_BY_ID);
        try {
            result = sender.sendMessage(request);
            if(result.isEmpty()){
                return null;
            }else{
                CommentRequestTo comment = result.get(0);
                return CommentMapper.instance.convertRequestToComment(comment);
            }
        }catch (JsonProcessingException e){
            return null;
        }
    }

    @Override
    public List<Comment> getAll() {
        List<CommentRequestTo> result = new ArrayList<>();
        KafkaRequest request= new KafkaRequest();
        request.setKey(UUID.randomUUID());
        request.setRequestMethod(Methods.GET_ALL);
        try {
            result = sender.sendMessage(request);
            if(result.isEmpty()){
                return null;
            }else{
                List<Comment> comments = new ArrayList<>();
                for(CommentRequestTo crto : result){
                    comments.add(CommentMapper.instance.convertFromDTO(crto));
                }
                return comments;
            }
        }catch (JsonProcessingException e){
            return null;
        }
    }
}
