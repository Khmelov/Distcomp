package com.sergey.orsik.service;

import com.sergey.orsik.dto.request.CreatorRequestTo;
import com.sergey.orsik.dto.response.CreatorResponseTo;

import java.util.List;

public interface CreatorService {

    List<CreatorResponseTo> findAll();

    CreatorResponseTo findById(Long id);

    CreatorResponseTo create(CreatorRequestTo request);

    CreatorResponseTo update(Long id, CreatorRequestTo request);

    void deleteById(Long id);
}
