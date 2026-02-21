package com.sergey.orsik.service;

import com.sergey.orsik.dto.request.CommentRequestTo;
import com.sergey.orsik.dto.response.CommentResponseTo;

import java.util.List;

public interface CommentService {

    List<CommentResponseTo> findAll();

    CommentResponseTo findById(Long id);

    CommentResponseTo create(CommentRequestTo request);

    CommentResponseTo update(Long id, CommentRequestTo request);

    void deleteById(Long id);
}
