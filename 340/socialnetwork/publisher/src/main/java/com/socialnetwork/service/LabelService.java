package com.socialnetwork.service;

import com.socialnetwork.dto.request.LabelRequestTo;
import com.socialnetwork.dto.response.LabelResponseTo;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import java.util.List;

public interface LabelService {
    List<LabelResponseTo> getAll();
    Page<LabelResponseTo> getAll(Pageable pageable);
    LabelResponseTo getById(Long id);
    LabelResponseTo create(LabelRequestTo request);
    LabelResponseTo update(Long id, LabelRequestTo request);
    void delete(Long id);
    boolean existsById(Long id);
    LabelResponseTo findByName(String name);
    boolean existsByName(String name);
}