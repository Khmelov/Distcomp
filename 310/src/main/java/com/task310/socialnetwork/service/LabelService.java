package com.task310.socialnetwork.service;

import com.task310.socialnetwork.dto.request.LabelRequestTo;
import com.task310.socialnetwork.dto.response.LabelResponseTo;
import java.util.List;

public interface LabelService {
    List<LabelResponseTo> getAll();
    LabelResponseTo getById(Long id);
    LabelResponseTo create(LabelRequestTo request);
    LabelResponseTo update(Long id, LabelRequestTo request);
    void delete(Long id);
    boolean existsById(Long id);
}