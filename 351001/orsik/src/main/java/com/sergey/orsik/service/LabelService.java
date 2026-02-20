package com.sergey.orsik.service;

import com.sergey.orsik.dto.request.LabelRequestTo;
import com.sergey.orsik.dto.response.LabelResponseTo;

import java.util.List;

public interface LabelService {

    List<LabelResponseTo> findAll();

    LabelResponseTo findById(Long id);

    LabelResponseTo create(LabelRequestTo request);

    LabelResponseTo update(Long id, LabelRequestTo request);

    void deleteById(Long id);
}
