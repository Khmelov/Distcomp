package org.example.task310rest.service;

import java.util.List;
import org.example.task310rest.dto.LabelRequestTo;
import org.example.task310rest.dto.LabelResponseTo;

public interface LabelService {
    LabelResponseTo create(LabelRequestTo request);

    LabelResponseTo getById(Long id);

    List<LabelResponseTo> getAll();

    LabelResponseTo update(Long id, LabelRequestTo request);

    void delete(Long id);
}


