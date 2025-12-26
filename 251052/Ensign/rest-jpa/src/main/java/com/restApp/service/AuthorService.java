package com.restApp.service;

import com.restApp.dto.AuthorRequestTo;
import com.restApp.dto.AuthorResponseTo;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface AuthorService {
    AuthorResponseTo create(AuthorRequestTo request);

    AuthorResponseTo update(Long id, AuthorRequestTo request);

    void delete(Long id);

    AuthorResponseTo findById(Long id);

    Page<AuthorResponseTo> findAll(Pageable pageable);
}
