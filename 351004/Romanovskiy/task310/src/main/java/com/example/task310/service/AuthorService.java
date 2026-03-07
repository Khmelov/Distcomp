package com.example.task310.service;

import com.example.task310.domain.dto.request.AuthorRequestTo;
import com.example.task310.domain.dto.response.AuthorResponseTo;
import java.util.List;

public interface AuthorService {
    AuthorResponseTo create(AuthorRequestTo request);
    List<AuthorResponseTo> findAll();
    AuthorResponseTo findById(Long id);
    AuthorResponseTo update(AuthorRequestTo request);
    void deleteById(Long id);
}