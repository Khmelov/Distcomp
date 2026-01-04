package com.blog.service;

import com.blog.dto.request.TagRequestTo;
import com.blog.dto.response.TagResponseTo;
import java.util.List;

public interface TagService {
    List<TagResponseTo> getAll();
    TagResponseTo getById(Long id);
    TagResponseTo create(TagRequestTo request);
    TagResponseTo update(Long id, TagRequestTo request);
    void delete(Long id);
    boolean existsById(Long id);
}