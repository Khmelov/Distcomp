package by.ryabchikov.tweet_service.service.impl;

import by.ryabchikov.tweet_service.client.CommentClient;
import by.ryabchikov.tweet_service.dto.comment.CommentRequestTo;
import by.ryabchikov.tweet_service.dto.comment.CommentResponseTo;
import by.ryabchikov.tweet_service.dto.comment.CommentUpdateRequestTo;
import by.ryabchikov.tweet_service.service.CommentService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class CommentServiceImpl implements CommentService {
    private final CommentClient commentClient;

    @Override
    @Transactional
    public CommentResponseTo create(CommentRequestTo commentRequestTo) {
        System.out.println("Comment service, comment request: " + commentRequestTo);
        CommentResponseTo commentResponseTo = commentClient.create(commentRequestTo);
        System.out.println("Comment service, comment response: " + commentResponseTo);
        return commentResponseTo;
    }

    @Override
    @Transactional
    public List<CommentResponseTo> readAll() {
        return commentClient.readAll();
    }

    @Override
    @Transactional
    public CommentResponseTo readById(Long id) {
        return commentClient.readById(id);
    }

    @Override
    @Transactional
    public CommentResponseTo update(CommentUpdateRequestTo commentUpdateRequestTo) {
        return commentClient.update(commentUpdateRequestTo);
    }

    @Override
    @Transactional
    public void deleteById(Long id) {
        commentClient.deleteById(id);
    }
}
