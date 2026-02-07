package com.example.demo.service;

import com.example.demo.dto.requests.IssueRequestTo;
import com.example.demo.dto.requests.MarkRequestTo;
import com.example.demo.dto.responses.IssueResponseTo;
import com.example.demo.dto.responses.MarkResponseTo;
import com.example.demo.model.Mark;
import com.example.demo.repository.MarkRepository;
import org.springframework.stereotype.Service;

import java.util.List;
@Service
public class MarkService {
    private final MarkRepository repository;
    private final EntityMapper mapper;

    public MarkService(MarkRepository repository, EntityMapper mapper) {
        this.repository = repository;
        this.mapper = mapper;
    }

    public MarkResponseTo create(MarkRequestTo dto){
        Mark mark = mapper.toMarkEntity(dto);
        Mark saved = repository.save(mark);
        return mapper.toMarkResponse(mark);
    }

    public MarkResponseTo findById(Long id){
        Mark entity = repository.findById(id)
                .orElseThrow(() -> new RuntimeException("Mark not found"));
        return mapper.toMarkResponse(entity);
    }

    public List<MarkResponseTo> findAll(){
        return repository.findAll().stream().map(mapper::toMarkResponse).toList();
    }

    public MarkResponseTo update(Long id, MarkRequestTo dto){
        Mark entity = repository.findById(id)
                .orElseThrow(() -> new RuntimeException("Mark not found"));
        mapper.updateEntity(dto, entity);
        return mapper.toMarkResponse(repository.save(entity));
    }

    public void delete(Long id){
        if(!repository.existsById(id)){
            throw new RuntimeException("Mark not found: " + id);
        }
        repository.deleteById(id);
    }
}
