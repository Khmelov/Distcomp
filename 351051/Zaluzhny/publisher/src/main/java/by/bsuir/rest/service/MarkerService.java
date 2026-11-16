package by.bsuir.rest.service;

import by.bsuir.rest.dto.request.MarkerRequestTo;
import by.bsuir.rest.dto.response.MarkerResponseTo;

import java.util.List;

public interface MarkerService {

    MarkerResponseTo create(MarkerRequestTo request);

    MarkerResponseTo getById(Long id);

    List<MarkerResponseTo> getAll();

    MarkerResponseTo update(Long pathId, MarkerRequestTo request);

    void delete(Long id);
}
