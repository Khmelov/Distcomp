package com.example.discussion.service;

import com.example.discussion.dto.MarkRequestTo;
import com.example.discussion.dto.MarkResponseTo;
import com.example.discussion.mapper.MarkMapper;
import com.example.discussion.model.Mark;
import com.example.discussion.repository.MarkRepository;
import jakarta.persistence.EntityNotFoundException;
import org.mapstruct.factory.Mappers;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class MarkService {
    private final MarkMapper mapper = Mappers.getMapper(MarkMapper.class);
    private final MarkRepository repository;
    @Autowired
    public MarkService(MarkRepository markRepository) {
        this.repository = markRepository;
    }

    public MarkResponseTo createMark(MarkRequestTo request){
        Mark mark = mapper.toEntity(request);
        mark = repository.save(mark);
        return mapper.toDto(mark);
    }
    public List<MarkResponseTo> findAll() {
        return repository.findAll().stream()
                .map(mapper::toDto)
                .collect(Collectors.toList());
    }

    public MarkResponseTo findById(Long id) {
        Optional<Mark> mark = repository.findById(id);
        return mark.map(mapper::toDto).orElse(null);
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
    public MarkResponseTo save(MarkRequestTo markRequestTo) {
        Mark mark = mapper.toEntity(markRequestTo);
        Mark savedMark = repository.save(mark);
        return mapper.toDto(savedMark);
    }
    public MarkResponseTo updateMark(MarkRequestTo request){
        Mark mark = repository.findById(request.getId()).orElseThrow(()->new EntityNotFoundException(("Creator not found")));
        mapper.updateEntityFromDto(request, mark);
        Mark updatedMark = repository.save(mark);
        return mapper.toDto(updatedMark);
    }
    public MarkResponseTo update(MarkRequestTo markRequestTo) {
        Mark existingMark = repository.findById(markRequestTo.getId()).orElseThrow(() -> new RuntimeException("Mark not found"));
        mapper.updateEntityFromDto(markRequestTo, existingMark);
        Mark updatedMark = repository.save(existingMark);
        return mapper.toDto(updatedMark);
    }

}
