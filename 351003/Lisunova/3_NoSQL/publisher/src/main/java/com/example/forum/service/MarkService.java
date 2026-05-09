package com.example.forum.service;

import com.example.forum.dto.request.MarkRequestTo;
import com.example.forum.dto.response.MarkResponseTo;

import java.util.List;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface MarkService {

    MarkResponseTo create(MarkRequestTo request);

    MarkResponseTo getById(Long id);

    Page<MarkResponseTo> getAll(Pageable pageable);

    MarkResponseTo update(Long id, MarkRequestTo request);

    void delete(Long id);
}
