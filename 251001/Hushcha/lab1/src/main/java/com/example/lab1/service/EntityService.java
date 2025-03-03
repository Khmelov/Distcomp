package com.example.lab1.service;

import java.util.List;

public interface EntityService<Request, Response> {

        Response create(Request entityDTO);

        Response getById(Long id);

        List<Response> getAll();

        Response update(Request entityDTO);

        void deleteById(Long id);
}
