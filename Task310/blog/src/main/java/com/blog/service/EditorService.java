package com.blog.service;

import com.blog.dto.request.EditorRequestTo;
import com.blog.dto.response.EditorResponseTo;
import java.util.List;

public interface EditorService {
    List<EditorResponseTo> getAll();
    EditorResponseTo getById(Long id);
    EditorResponseTo create(EditorRequestTo request);
    EditorResponseTo update(Long id, EditorRequestTo request);
    void delete(Long id);
    boolean existsById(Long id);
}