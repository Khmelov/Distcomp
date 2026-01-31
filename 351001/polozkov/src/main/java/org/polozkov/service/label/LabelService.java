package org.polozkov.service.label;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.polozkov.dto.label.LabelRequestTo;
import org.polozkov.dto.label.LabelResponseTo;
import org.polozkov.entity.label.Label;
import org.polozkov.mapper.label.LabelMapper;
import org.polozkov.repository.label.LabelRepository;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;

import java.util.List;
import java.util.stream.Collectors;

@Service
@Validated
@RequiredArgsConstructor
public class LabelService {

    private final LabelRepository labelRepository;
    private final LabelMapper labelMapper;

    public List<LabelResponseTo> getAllLabels() {
        return labelRepository.findAll().stream()
                .map(labelMapper::labelToResponseDto)
                .collect(Collectors.toList());
    }

    public LabelResponseTo getLabelById(Long id) {
        return labelRepository.findById(id)
                .map(labelMapper::labelToResponseDto)
                .orElseThrow(() -> new RuntimeException("Label not found with id: " + id));
    }

    public LabelResponseTo createLabel(@Valid LabelRequestTo labelRequest) {
        // Проверка уникальности имени
        labelRepository.findByName(labelRequest.getName())
                .ifPresent(label -> {
                    throw new RuntimeException("Label with name " + labelRequest.getName() + " already exists");
                });

        Label label = labelMapper.requestDtoToLabel(labelRequest);
        Label savedLabel = labelRepository.save(label);
        return labelMapper.labelToResponseDto(savedLabel);
    }

    public LabelResponseTo updateLabel(@Valid LabelRequestTo labelRequest) {
        if (!labelRepository.existsById(labelRequest.getId())) {
            throw new RuntimeException("Label not found with id: " + labelRequest.getId());
        }

        Label label = labelMapper.requestDtoToLabel(labelRequest);
        Label updatedLabel = labelRepository.update(label);
        return labelMapper.labelToResponseDto(updatedLabel);
    }

    public void deleteLabel(Long id) {
        if (!labelRepository.existsById(id)) {
            throw new RuntimeException("Label not found with id: " + id);
        }
        labelRepository.deleteById(id);
    }
}