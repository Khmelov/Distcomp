package com.example.task330.service;

import com.example.task330.domain.dto.request.AuthorRequestTo;
import com.example.task330.domain.dto.response.AuthorResponseTo;
import java.util.List;

public interface AuthorService {
    AuthorResponseTo create(AuthorRequestTo request);
    List<AuthorResponseTo> findAll(int page, int size); 
    AuthorResponseTo findById(Long id);
    AuthorResponseTo update(AuthorRequestTo request);
    void deleteById(Long id);
}