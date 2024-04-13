package com.example.rv.impl.note;

import com.example.rv.api.repository.CrudRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
public class NoteService {

    public final CrudRepository<Note, Long> noteCrudRepository;

    public final NoteMapperImpl noteMapper;
}
