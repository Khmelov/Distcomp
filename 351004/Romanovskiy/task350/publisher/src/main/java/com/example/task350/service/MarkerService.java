package com.example.task350.service;

import com.example.task350.domain.dto.request.MarkerRequestTo;
import com.example.task350.domain.dto.response.MarkerResponseTo;
import java.util.List;

public interface MarkerService {
    MarkerResponseTo create(MarkerRequestTo request);
    List<MarkerResponseTo> findAll(int page, int size); // ИСПРАВИТЬ ТУТ
    MarkerResponseTo findById(Long id);
    MarkerResponseTo update(MarkerRequestTo request);
    void deleteById(Long id);
}