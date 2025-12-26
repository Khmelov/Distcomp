package com.restApp.service;

import com.restApp.dto.MarkRequestTo;
import com.restApp.dto.MarkResponseTo;

import java.util.List;

public interface MarkService {
    MarkResponseTo create(MarkRequestTo request);

    MarkResponseTo update(Long id, MarkRequestTo request);

    void delete(Long id);

    MarkResponseTo findById(Long id);

    List<MarkResponseTo> findAll();
}
