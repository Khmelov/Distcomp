package org.polozkov.service.label;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.polozkov.dto.label.LabelRequestTo;
import org.polozkov.dto.label.LabelResponseTo;
import org.polozkov.entity.label.Label;
import org.polozkov.exception.BadRequestException;
import org.polozkov.exception.ForbiddenException;
import org.polozkov.mapper.label.LabelMapper;
import org.polozkov.repository.label.LabelRepository;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;

import java.util.ArrayList;
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
        return labelRepository.byId(id);
    }

    public LabelResponseTo createLabel(@Valid LabelRequestTo labelRequest) {
        if (labelRepository.findByName(labelRequest.getName()).isPresent()) {
            throw new ForbiddenException("Label with name " + labelRequest.getName() + " already exists");
        }

        Label label = labelMapper.requestDtoToLabel(labelRequest);

        label.setIssues(new ArrayList<>());

        Label savedLabel = labelRepository.save(label);
        return labelMapper.labelToResponseDto(savedLabel);
    }

    public LabelResponseTo updateLabel(@Valid LabelRequestTo labelRequest) {
        Label existingLabel = labelRepository.byId(labelRequest.getId());

        Label label = labelMapper.updateLabel(existingLabel, labelRequest);

        label.setIssues(existingLabel.getIssues());

        Label updatedLabel = labelRepository.save(label);
        return labelMapper.labelToResponseDto(updatedLabel);
    }

    public void deleteLabel(Long id) {
        Label label = labelRepository.byId(id);

        label.getIssues().forEach(issue ->
                issue.getLabels().removeIf(l -> l.getId().equals(id))
        );

        labelRepository.deleteById(id);
    }

    public void addLabelToIssue(Long labelId, org.polozkov.entity.issue.Issue issue) {
        Label label = labelRepository.byId(labelId);
        label.getIssues().add(issue);
        labelRepository.save(label);
    }

    public void removeLabelFromIssue(Long labelId, org.polozkov.entity.issue.Issue issue) {
        Label label = labelRepository.byId(labelId);
        label.getIssues().removeIf(i -> i.getId().equals(issue.getId()));
        labelRepository.save(label);
    }
}