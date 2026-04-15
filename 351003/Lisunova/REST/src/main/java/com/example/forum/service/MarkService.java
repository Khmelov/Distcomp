package com.example.forum.service;

import com.example.forum.dto.request.MarkRequestTo;
import com.example.forum.dto.response.MarkResponseTo;

import java.util.List;

public interface MarkService {

    MarkResponseTo create(MarkRequestTo request);

    MarkResponseTo getById(Long id);

    List<MarkResponseTo> getAll();

    MarkResponseTo update(Long id, MarkRequestTo request);

    void delete(Long id);
}
