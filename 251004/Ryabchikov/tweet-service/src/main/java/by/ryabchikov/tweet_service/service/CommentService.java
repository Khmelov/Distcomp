package by.ryabchikov.tweet_service.service;

import by.ryabchikov.tweet_service.dto.comment.CommentRequestTo;
import by.ryabchikov.tweet_service.dto.comment.CommentResponseTo;
import by.ryabchikov.tweet_service.dto.comment.CommentUpdateRequestTo;

import java.util.List;

public interface CommentService {
    CommentResponseTo create(CommentRequestTo commentRequestTo);

    List<CommentResponseTo> readAll();

    CommentResponseTo readById(Long id);

    CommentResponseTo update(CommentUpdateRequestTo commentUpdateRequestTo);

    void deleteById(Long id);
}
