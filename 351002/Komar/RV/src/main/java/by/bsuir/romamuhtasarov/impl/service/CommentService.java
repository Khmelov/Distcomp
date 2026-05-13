package by.bsuir.romamuhtasarov.impl.service;


import by.bsuir.romamuhtasarov.api.Service;
import by.bsuir.romamuhtasarov.api.CommentMapper;
import by.bsuir.romamuhtasarov.impl.bean.Comment;
import by.bsuir.romamuhtasarov.impl.dto.*;
import by.bsuir.romamuhtasarov.impl.repository.CommentRepository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
public class CommentService implements Service<CommentResponseTo, CommentRequestTo> {
    @Autowired
    private CommentRepository commentRepository;

    public CommentService() {

    }

    public List<CommentResponseTo> getAll() {
        List<Comment> commentList = commentRepository.getAll();
        List<CommentResponseTo> resultList = new ArrayList<>();
        for (int i = 0; i < commentList.size(); i++) {
            resultList.add(CommentMapper.INSTANCE.CommentToCommentResponseTo(commentList.get(i)));
        }
        return resultList;
    }

    public CommentResponseTo update(CommentRequestTo updatingComment) {
        Comment comment = CommentMapper.INSTANCE.CommentRequestToToComment(updatingComment);
        if (validateComment(comment)) {
            boolean result = commentRepository.update(comment);
            CommentResponseTo responseTo = result ? CommentMapper.INSTANCE.CommentToCommentResponseTo(comment) : null;
            return responseTo;
        } else return new CommentResponseTo();
        //return responseTo;
    }

    public CommentResponseTo get(long id) {
        return CommentMapper.INSTANCE.CommentToCommentResponseTo(commentRepository.get(id));
    }

    public CommentResponseTo delete(long id) {
        return CommentMapper.INSTANCE.CommentToCommentResponseTo(commentRepository.delete(id));
    }

    public CommentResponseTo add(CommentRequestTo commentRequestTo) {
        Comment comment = CommentMapper.INSTANCE.CommentRequestToToComment(commentRequestTo);
        return CommentMapper.INSTANCE.CommentToCommentResponseTo(commentRepository.insert(comment));
    }

    private boolean validateComment(Comment comment) {
        String name = comment.getContent();
        if (name.length() >= 2 && name.length() <= 32) return true;
        return false;
    }
}