package com.restApp.service;

import com.restApp.dto.CommentRequestTo;
import com.restApp.dto.CommentResponseTo;

import java.util.List;

public interface CommentService {
    CommentResponseTo create(CommentRequestTo request);

    CommentResponseTo update(Long id, CommentRequestTo request);

    void delete(Long id);

    CommentResponseTo findById(Long id);

    List<CommentResponseTo> findAll();
}
