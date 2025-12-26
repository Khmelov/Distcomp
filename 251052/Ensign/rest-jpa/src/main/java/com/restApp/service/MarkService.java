package com.restApp.service;

import com.restApp.dto.MarkRequestTo;
import com.restApp.dto.MarkResponseTo;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface MarkService {
    MarkResponseTo create(MarkRequestTo request);

    MarkResponseTo update(Long id, MarkRequestTo request);

    void delete(Long id);

    MarkResponseTo findById(Long id);

    Page<MarkResponseTo> findAll(Pageable pageable);

    java.util.List<MarkResponseTo> findAll();
}
