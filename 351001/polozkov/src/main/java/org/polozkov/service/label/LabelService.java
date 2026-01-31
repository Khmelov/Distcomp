package org.polozkov.service.label;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.polozkov.dto.label.LabelRequestTo;
import org.polozkov.dto.label.LabelResponseTo;
import org.polozkov.entity.label.Label;
import org.polozkov.exception.BadRequestException;
import org.polozkov.exception.NotFoundException;
import org.polozkov.mapper.label.LabelMapper;
import org.polozkov.repository.label.LabelRepository;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;

import java.util.List;

@Service
@Validated
@RequiredArgsConstructor
public class LabelService {

    private final LabelRepository labelRepository;
    private final LabelMapper labelMapper;

    public List<LabelResponseTo> getAllLabels() {
        return labelRepository.findAll().stream()
                .map(labelMapper::labelToResponseDto)
                .toList();
    }

    public LabelResponseTo getLabel(Long id) {
        return labelMapper.labelToResponseDto(getLabelById(id));
    }

    public Label getLabelById(Long id) {
        return labelRepository.getById(id);
    }

    public LabelResponseTo createLabel(@Valid LabelRequestTo labelRequest) {
        if (labelRepository.findByName(labelRequest.getName()).isPresent()) {
            throw new BadRequestException("Label with name " + labelRequest.getName() + " already exists");
        }

        Label label = labelMapper.requestDtoToLabel(labelRequest);
        Label savedLabel = labelRepository.save(label);
        return labelMapper.labelToResponseDto(savedLabel);
    }

    public LabelResponseTo updateLabel(@Valid LabelRequestTo labelRequest) {
        labelRepository.getById(labelRequest.getId());

        Label label = labelRepository.findById(labelRequest.getId()).orElseThrow(() -> new NotFoundException("Label not found with id: " + labelRequest.getId()));
        label = labelMapper.updateLabel(label, labelRequest);
        return labelMapper.labelToResponseDto(label);
    }

    public void deleteLabel(Long id) {
        labelRepository.getById(id);
        labelRepository.deleteById(id);
    }
}