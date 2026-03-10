package com.example.task310.service;

import com.example.task310.domain.dto.request.ReactionRequestTo;
import com.example.task310.domain.dto.response.ReactionResponseTo;
import java.util.List;

public interface ReactionService {
    ReactionResponseTo create(ReactionRequestTo request);
    List<ReactionResponseTo> findAll();
    ReactionResponseTo findById(Long id);
    ReactionResponseTo update(ReactionRequestTo request);
    void deleteById(Long id);
}