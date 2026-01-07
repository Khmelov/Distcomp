package com.group310971.gormash.service;

import com.group310971.gormash.dto.MarkRequestTo;
import com.group310971.gormash.dto.MarkResponseTo;
import com.group310971.gormash.mapper.MarkMapper;
import com.group310971.gormash.model.Mark;
import com.group310971.gormash.repository.MarkRepository;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.LinkedList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class MarkService {
    private final MarkRepository markRepository;
    private final MarkMapper markMapper = MarkMapper.INSTANCE;

    public MarkResponseTo createMark(@Valid MarkRequestTo markRequestTo){
        Mark mark = markMapper.toEntity(markRequestTo);
        Mark savedMark = markRepository.save(mark);
        return markMapper.toResponse(savedMark);
    }

    public MarkResponseTo updateMark(Long id, @Valid MarkRequestTo markRequestTo){
        if (id == null)
            id = markRequestTo.getId();
        else
            markRequestTo.setId(id);
        if (id == null) {
            throw new RuntimeException("Mark id cannot be null for update");
        }
        var optional = markRepository.findById(id);
        if (optional.isEmpty())
            throw new RuntimeException("Mark not exists");
        Mark persistence = optional.get();
        persistence.setName(markMapper.toEntity(markRequestTo).getName());
        Mark updatedMark = markRepository.save(persistence);
        return markMapper.toResponse(updatedMark);
    }

    public MarkResponseTo getMarkById(Long id) {
        return markRepository.findById(id)
                .map(markMapper::toResponse)
                .orElseThrow(() -> new RuntimeException("Mark not found with id: " + id));
    }

    public List<MarkResponseTo> getAllMarks(){
        LinkedList<MarkResponseTo> list = new LinkedList<>();
        for (Mark mark : markRepository.findAll()){
            list.add(markMapper.toResponse(mark));
        }
        return list;
    }

    public MarkResponseTo deleteMark(Long id) {
        var optional = markRepository.findById(id);
        if (optional.isEmpty())
            throw new RuntimeException("Mark not exists");
        Mark mark = optional.get();
        markRepository.delete(mark);
        return markMapper.toResponse(mark);
    }
}
