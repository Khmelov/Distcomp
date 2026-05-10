package org.example.newsapi.service;

import lombok.RequiredArgsConstructor;
import org.example.newsapi.dto.request.CommentRequestTo;
import org.example.newsapi.dto.response.CommentResponseTo;
import org.example.newsapi.exception.NotFoundException;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class CommentService {

    private final KafkaCommentService kafkaCommentService;

    public CommentResponseTo create(CommentRequestTo request) {
        return kafkaCommentService.createComment(request);
    }


    public List<CommentResponseTo> findAll() {
        // TODO: через Kafka или WebClient
        throw new UnsupportedOperationException("Not implemented yet");
    }

    public CommentResponseTo findById(Long id) {
        // TODO
        throw new UnsupportedOperationException("Not implemented yet");
    }

    public CommentResponseTo update(Long id, CommentRequestTo request) {
        // TODO
        throw new UnsupportedOperationException("Not implemented yet");
    }

    public void delete(Long id) {
        // TODO
        throw new UnsupportedOperationException("Not implemented yet");
    }
}