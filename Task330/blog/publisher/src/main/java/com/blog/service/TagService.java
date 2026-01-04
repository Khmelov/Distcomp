package com.blog.service;

import com.blog.dto.request.TagRequestTo;
import com.blog.dto.response.TagResponseTo;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface TagService {
    List<TagResponseTo> getAll();
    Page<TagResponseTo> getAll(Pageable pageable);
    TagResponseTo getById(Long id);
    TagResponseTo create(TagRequestTo request);
    TagResponseTo update(Long id, TagRequestTo request);
    void delete(Long id);
    boolean existsById(Long id);

    // Дополнительные методы
    TagResponseTo findByName(String name);
    boolean existsByName(String name);
}