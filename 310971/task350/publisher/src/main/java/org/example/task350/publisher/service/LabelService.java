package org.example.task350.publisher.service;

import java.util.List;
import org.example.task350.publisher.dto.LabelRequestTo;
import org.example.task350.publisher.dto.LabelResponseTo;

public interface LabelService {
    LabelResponseTo create(LabelRequestTo request);

    LabelResponseTo getById(Long id);

    List<LabelResponseTo> getAll();

    LabelResponseTo update(Long id, LabelRequestTo request);

    void delete(Long id);
}

