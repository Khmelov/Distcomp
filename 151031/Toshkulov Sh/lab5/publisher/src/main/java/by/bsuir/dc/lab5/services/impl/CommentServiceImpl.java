package by.bsuir.dc.lab5.services.impl;


import by.bsuir.dc.lab5.dto.CommentRequestTo;
import by.bsuir.dc.lab5.dto.mappers.CommentMapper;
import by.bsuir.dc.lab5.entities.Comment;
import by.bsuir.dc.lab5.entities.News;
import by.bsuir.dc.lab5.kafka.KafkaRequest;
import by.bsuir.dc.lab5.kafka.KafkaPublisherMessaging;
import by.bsuir.dc.lab5.kafka.Methods;
import by.bsuir.dc.lab5.redis.RedisCommentRepository;
import by.bsuir.dc.lab5.services.interfaces.CommentService;
import by.bsuir.dc.lab5.services.repos.NewsRepository;
import com.fasterxml.jackson.core.JsonProcessingException;
import org.springframework.beans.factory.annotation.Autowired;
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

    @Autowired
    private RedisCommentRepository redisRepos;

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
                    Comment saved = CommentMapper.instance.convertRequestToComment(updatedComment);
                    redisRepos.add(saved);
                    return saved;
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
        CommentRequestTo commentRequestTo = new CommentRequestTo();
        commentRequestTo.setId(id);
        KafkaRequest request= new KafkaRequest();
        request.setKey(UUID.randomUUID());
        request.getDtoToTransfer().add(commentRequestTo);
        request.setRequestMethod(Methods.DELETE);
        try {
            redisRepos.delete(id);
            sender.sendMessage(request);
        }catch (JsonProcessingException e){
            return;
        }

    }

    @Override
    public Comment update(Comment comment) {
        Optional<News> relatedNews = newsRepo.findById(comment.getNewsId());
        if(relatedNews.isPresent()) {
            Optional<Comment> cachedComment = redisRepos.getById(comment.getId());
            if(cachedComment.isPresent() && comment.equals(cachedComment.get())){
                return comment;
            }
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
                    Comment updated = CommentMapper.instance.convertRequestToComment(updatedComment);
                    redisRepos.update(updated);
                    return updated;
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
        Optional<Comment> comment = redisRepos.getById(id);
        if(comment.isPresent()){
            return comment.get();
        } else {
            List<CommentRequestTo> result = new ArrayList<>();
            CommentRequestTo commentRequestTo = new CommentRequestTo();
            commentRequestTo.setId(id);
            KafkaRequest request = new KafkaRequest();
            request.setKey(UUID.randomUUID());
            request.getDtoToTransfer().add(commentRequestTo);
            request.setRequestMethod(Methods.GET_BY_ID);
            try {
                result = sender.sendMessage(request);
                if (result.isEmpty()) {
                    return null;
                } else {
                    CommentRequestTo commentTo = result.get(0);
                    comment = Optional.of(CommentMapper.instance.convertRequestToComment(commentTo));
                    redisRepos.add(comment.get());
                    return comment.get();
                }
            } catch (JsonProcessingException e) {
                return null;
            }
        }
    }

    @Override
    public List<Comment> getAll() {
        List<Comment> comments = redisRepos.getAll();
        if(!comments.isEmpty()){
            return comments;
        }else {
            List<CommentRequestTo> result = new ArrayList<>();
            KafkaRequest request = new KafkaRequest();
            request.setKey(UUID.randomUUID());
            request.setRequestMethod(Methods.GET_ALL);
            try {
                result = sender.sendMessage(request);
                if (result.isEmpty()) {
                    return new ArrayList<>();
                } else {
                    for (CommentRequestTo commentRequestTo : result) {
                        comments.add(CommentMapper.instance.convertFromDTO(commentRequestTo));
                        redisRepos.add(CommentMapper.instance.convertFromDTO(commentRequestTo));
                    }
                    return comments;
                }
            } catch (JsonProcessingException e) {
                return new ArrayList<>();
            }
        }
    }
}
