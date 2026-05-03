package com.example.task330.service; // Проверь этот пакет!

import com.example.task330.domain.dto.request.ReactionRequestTo;
import com.example.task330.domain.dto.response.ReactionResponseTo;
import java.util.List;

public interface ReactionService {
    ReactionResponseTo create(ReactionRequestTo request);
    List<ReactionResponseTo> findAll(int page, int size);
    ReactionResponseTo findById(Long id);
    ReactionResponseTo update(ReactionRequestTo request);
    void deleteById(Long id);
}