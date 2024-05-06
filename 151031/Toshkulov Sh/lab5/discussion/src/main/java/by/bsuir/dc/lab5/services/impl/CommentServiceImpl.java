package by.bsuir.dc.lab5.services.impl;

import by.bsuir.dc.lab5.entities.Comment;
import by.bsuir.dc.lab5.services.interfaces.CommentDiscService;
import by.bsuir.dc.lab5.services.repos.CommentDicsRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
public class CommentServiceImpl implements CommentDiscService {

    @Autowired
    private CommentDicsRepository repos;

    @Override
    public Comment add(Comment comment) {
        comment.setId(Math.abs(UUID.randomUUID().getLeastSignificantBits()));
        return repos.save(comment);
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
        return repos.save(comment);
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
