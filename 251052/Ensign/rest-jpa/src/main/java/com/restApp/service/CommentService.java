package com.restApp.service;

import com.restApp.dto.CommentRequestTo;
import com.restApp.dto.CommentResponseTo;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface CommentService {
    CommentResponseTo create(CommentRequestTo request);

    CommentResponseTo update(Long id, CommentRequestTo request);

    void delete(Long id);

    CommentResponseTo findById(Long id);

    Page<CommentResponseTo> findAll(Pageable pageable);
}
