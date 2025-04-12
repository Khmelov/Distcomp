package com.example.rest.service;

import com.example.rest.dto.requestDto.LabelRequestTo;
import com.example.rest.dto.responseDto.LabelResponseTo;
import com.example.rest.dto.updateDto.LabelUpdateTo;
import com.example.rest.mapper.LabelMapper;
import com.example.rest.repository.LabelRepo;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@AllArgsConstructor
public class LabelService {

    private final LabelRepo labelRepo;
    private final LabelMapper labelMapper;

    public List<LabelResponseTo> getAll() {
        return labelRepo
                .getAll()
                .map(labelMapper::ToLabelResponseTo)
                .toList();
    }
    public LabelResponseTo get(long id) {
        return labelRepo
                .get(id)
                .map(labelMapper::ToLabelResponseTo)
                .orElse(null);
    }
    public LabelResponseTo create(LabelRequestTo input) {
        return labelRepo
                .create(labelMapper.ToLabel(input))
                .map(labelMapper::ToLabelResponseTo)
                .orElseThrow();
    }
    public LabelResponseTo update(LabelUpdateTo input) {
        return labelRepo
                .update(labelMapper.ToLabel(input))
                .map(labelMapper::ToLabelResponseTo)
                .orElseThrow();
    }
    public boolean delete(long id) {
        return labelRepo.delete(id);
    }
}
