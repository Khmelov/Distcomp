package by.ryabchikov.comment_service.service.impl;

import by.ryabchikov.comment_service.client.TweetClient;
import by.ryabchikov.comment_service.dto.CommentRequestTo;
import by.ryabchikov.comment_service.dto.CommentResponseTo;
import by.ryabchikov.comment_service.dto.CommentUpdateRequestTo;
import by.ryabchikov.comment_service.entity.Comment;
import by.ryabchikov.comment_service.exception.CommentNotCreatedException;
import by.ryabchikov.comment_service.exception.CommentNotFoundException;
import by.ryabchikov.comment_service.mapper.CommentMapper;
import by.ryabchikov.comment_service.repository.CommentRepository;
import by.ryabchikov.comment_service.service.CommentService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class CommentServiceImpl implements CommentService {
    private final CommentRepository commentRepository;
    private final CommentMapper commentMapper;
    private final TweetClient tweetClient;

    @Override
    public CommentResponseTo create(CommentRequestTo commentRequestTo) {
        if(tweetClient.readById(commentRequestTo.tweetId()) == null) {
            throw CommentNotCreatedException.byInvalidTweetId(commentRequestTo.tweetId());
        }

        Comment comment = commentMapper.toComment(commentRequestTo);
        comment.setCountry("Default");
        comment.setId((long) (Math.random() * 10000000));

        return commentMapper.toCommentResponseTo(
                commentRepository.save(comment)
        );
    }

    @Override
    public List<CommentResponseTo> readAll() {
        ArrayList<CommentResponseTo> commentResponseTos = new ArrayList<>();
        commentRepository.findAll().forEach(comment -> {
            CommentResponseTo commentResponseTo = commentMapper.toCommentResponseTo(comment);
            commentResponseTos.add(commentResponseTo);
        });
        return commentResponseTos;
    }

    @Override
    public CommentResponseTo readById(Long id) {
        return commentMapper.toCommentResponseTo(
                commentRepository.findById(id).orElseThrow(() -> CommentNotFoundException.byId(id))
        );
    }

    @Override
    public CommentResponseTo update(CommentUpdateRequestTo commentUpdateRequestTo) {
        long commentId = commentUpdateRequestTo.id();
        Comment comment =
                commentRepository.findById(commentId).orElseThrow(() -> CommentNotFoundException.byId(commentId));

        if(tweetClient.readById(commentUpdateRequestTo.tweetId()) == null) {
            throw CommentNotCreatedException.byInvalidTweetId(commentUpdateRequestTo.tweetId());
        }

        comment.setTweetId(commentUpdateRequestTo.tweetId());
        comment.setContent(commentUpdateRequestTo.content());
        commentRepository.save(comment);

        return commentMapper.toCommentResponseTo(comment);
    }

    @Override
    public void deleteById(Long id) {
        Comment comment = commentRepository.findById(id).orElseThrow(() -> CommentNotFoundException.byId(id));
        commentRepository.delete(comment);
    }
}
