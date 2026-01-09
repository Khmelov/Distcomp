package org.example.task310rest.service;

import java.util.List;
import org.example.task310rest.dto.WriterRequestTo;
import org.example.task310rest.dto.WriterResponseTo;

public interface WriterService {
    WriterResponseTo create(WriterRequestTo request);

    WriterResponseTo getById(Long id);

    List<WriterResponseTo> getAll();

    WriterResponseTo update(Long id, WriterRequestTo request);

    void delete(Long id);
}


