package by.bsuir.dc.lab1.service.impl;

import by.bsuir.dc.lab1.dto.*;
import by.bsuir.dc.lab1.dto.mappers.CommentMapper;
import by.bsuir.dc.lab1.entities.Comment;
import by.bsuir.dc.lab1.inmemory.CommentsTable;
import by.bsuir.dc.lab1.service.abst.ICommentService;
import org.springframework.stereotype.Component;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;

@Component
public class CommentService implements ICommentService {

    @Override
    public CommentResponseTo create(CommentRequestTo commentTo) {
        Comment comment = CommentMapper.instance.convertFromDTO(commentTo);
        comment = CommentsTable.getInstance().add(comment);
        if(comment != null){
            return CommentMapper.instance.convertToDTO(comment);
        } else {
            return null;
        }
    }

    @Override
    public CommentResponseTo getById(BigInteger id) {
        Comment comment = CommentsTable.getInstance().getById(id);
        if(comment != null){
            return CommentMapper.instance.convertToDTO(comment);
        } else {
            return null;
        }
    }

    @Override
    public List<CommentResponseTo> getAll() {
        List<Comment> comments = CommentsTable.getInstance().getAll();
        List<CommentResponseTo> authorsTo = new ArrayList<>();
        for(Comment comment : comments){
            authorsTo.add(CommentMapper.instance.convertToDTO(comment));
        }
        return authorsTo;
    }

    @Override
    public CommentResponseTo update(CommentRequestTo commentTo) {
        Comment updatedComment = CommentMapper.instance.convertFromDTO(commentTo);
        Comment comment = CommentsTable.getInstance().update(updatedComment);
        if(comment != null){
            return CommentMapper.instance.convertToDTO(comment);
        } else {
            return null;
        }
    }

    @Override
    public boolean delete(BigInteger id) {
        return CommentsTable.getInstance().delete(id);
    }
}
