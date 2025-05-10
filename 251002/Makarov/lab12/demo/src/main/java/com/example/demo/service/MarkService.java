package com.example.demo.service;

import com.example.demo.dto.CreatorRequestTo;
import com.example.demo.dto.CreatorResponseTo;
import com.example.demo.dto.MarkRequestTo;
import com.example.demo.dto.MarkResponseTo;
import com.example.demo.mapper.MarkMapper;
import com.example.demo.model.Creator;
import com.example.demo.model.Mark;
import com.example.demo.repository.MarkRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class MarkService {
    private final MarkMapper mapper;
    private final MarkRepository repository;

    public MarkResponseTo createMark(MarkRequestTo request){
        Mark mark = mapper.toEntity(request);
        mark = repository.save(mark);
        return mapper.toDto(mark);
    }

    public MarkResponseTo getMarkById(Long id){
        Mark mark = repository.findById(id)
                .orElseThrow(()->new EntityNotFoundException("Mark not found"));
        return mapper.toDto(mark);
    }
    public List<MarkResponseTo> getAllMarks(){
        return repository.findAll()
                .stream()
                .map(mapper::toDto)
                .collect(Collectors.toList());
    }
    public void deleteMark(Long id){
        if(!repository.existsById(id)){
            throw new EntityNotFoundException("Mark not found");
        }
        repository.deleteById(id);
    }
    public MarkResponseTo updateMark(MarkRequestTo request){
        Mark mark = repository.findById(request.getId()).orElseThrow(()->new EntityNotFoundException(("Creator not found")));
        mapper.updateEntityFromDto(request, mark);
        Mark updatedMark = repository.save(mark);
        return mapper.toDto(updatedMark);
    }
}
