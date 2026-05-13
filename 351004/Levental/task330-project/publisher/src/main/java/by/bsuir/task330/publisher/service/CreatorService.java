package by.bsuir.task330.publisher.service;

import by.bsuir.task330.publisher.domain.Creator;
import by.bsuir.task330.publisher.dto.request.CreatorRequestTo;
import by.bsuir.task330.publisher.dto.response.CreatorResponseTo;

import java.util.List;

public interface CreatorService {
    CreatorResponseTo create(CreatorRequestTo request);
    CreatorResponseTo update(CreatorRequestTo request);
    CreatorResponseTo findById(Long id);
    List<CreatorResponseTo> findAll(Integer page, Integer size, String sort, String filter);
    void delete(Long id);

    Creator requireEntity(Long id);
}
