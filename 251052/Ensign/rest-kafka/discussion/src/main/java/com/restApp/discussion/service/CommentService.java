package com.restApp.discussion.service;

import com.restApp.discussion.dto.CommentRequestTo;
import com.restApp.discussion.dto.CommentResponseTo;

import com.restApp.discussion.model.CommentState;

import java.util.List;

public interface CommentService {
    CommentResponseTo create(CommentRequestTo request);

    CommentResponseTo findById(Long id);

    List<CommentResponseTo> findAll();

    CommentResponseTo update(Long id, CommentRequestTo request);

    void delete(Long id);

    List<CommentResponseTo> getCommentsByNewsId(Long newsId);

    CommentResponseTo moderate(Long id, String country, CommentState state);
}
