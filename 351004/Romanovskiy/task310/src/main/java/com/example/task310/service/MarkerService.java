package com.example.task310.service;

import com.example.task310.domain.dto.request.MarkerRequestTo;
import com.example.task310.domain.dto.response.MarkerResponseTo;
import java.util.List;

public interface MarkerService {
    MarkerResponseTo create(MarkerRequestTo request);
    List<MarkerResponseTo> findAll();
    MarkerResponseTo findById(Long id);
    MarkerResponseTo update(MarkerRequestTo request);
    void deleteById(Long id);
}