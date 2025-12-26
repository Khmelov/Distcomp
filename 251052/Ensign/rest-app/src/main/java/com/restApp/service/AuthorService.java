package com.restApp.service;

import com.restApp.dto.AuthorRequestTo;
import com.restApp.dto.AuthorResponseTo;

import java.util.List;

public interface AuthorService {
    AuthorResponseTo create(AuthorRequestTo request);

    AuthorResponseTo update(Long id, AuthorRequestTo request);

    void delete(Long id);

    AuthorResponseTo findById(Long id);

    List<AuthorResponseTo> findAll();
}
