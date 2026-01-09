package org.example.task350.publisher.service;

import java.util.List;
import org.example.task350.publisher.dto.WriterRequestTo;
import org.example.task350.publisher.dto.WriterResponseTo;

public interface WriterService {
    WriterResponseTo create(WriterRequestTo request);

    WriterResponseTo getById(Long id);

    List<WriterResponseTo> getAll();

    WriterResponseTo update(Long id, WriterRequestTo request);

    void delete(Long id);
}

