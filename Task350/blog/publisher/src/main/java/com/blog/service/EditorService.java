package com.blog.service;

import com.blog.dto.request.EditorRequestTo;
import com.blog.dto.response.EditorResponseTo;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface EditorService {
    List<EditorResponseTo> getAll();
    Page<EditorResponseTo> getAll(Pageable pageable);
    EditorResponseTo getById(Long id);
    EditorResponseTo create(EditorRequestTo request);
    EditorResponseTo update(Long id, EditorRequestTo request);
    void delete(Long id);
    boolean existsById(Long id);
}