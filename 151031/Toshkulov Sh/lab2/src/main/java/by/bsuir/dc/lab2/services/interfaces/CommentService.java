package by.bsuir.dc.lab2.services.interfaces;

import by.bsuir.dc.lab2.entities.Comment;

import java.util.List;

public interface CommentService {
    Comment add(Comment editor);

    void delete(long id);

    Comment update(Comment editor);

    Comment getById(long id);

    List<Comment> getAll();
}
