package com.example.storyapp.service;

import com.example.storyapp.dto.LabelRequestTo;
import com.example.storyapp.dto.LabelResponseTo;
import com.example.storyapp.exception.AppException;
import com.example.storyapp.model.Label;
import com.example.storyapp.repository.InMemoryLabelRepository;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class LabelService {
    private final InMemoryLabelRepository repo;

    public LabelService(InMemoryLabelRepository repo) {
        this.repo = repo;
    }

    public List<LabelResponseTo> getAllLabels() {
        return repo.findAll().stream()
                .map(this::toResponse)
                .toList();
    }

    public LabelResponseTo getLabelById(@NotNull Long id) {
        return repo.findById(id)
                .map(this::toResponse)
                .orElseThrow(() -> new AppException("Label not found", 40403));
    }

    public LabelResponseTo createLabel(@Valid LabelRequestTo request) {
        Label label = toEntity(request);
        Label saved = repo.save(label);
        return toResponse(saved);
    }

    public LabelResponseTo updateLabel(@Valid LabelRequestTo request) {
        if (request.id() == null) {
            throw new AppException("ID required for update", 40003);
        }
        if (!repo.findById(request.id()).isPresent()) {
            throw new AppException("Label not found for update", 40403);
        }
        Label label = toEntity(request);
        Label updated = repo.save(label);
        return toResponse(updated);
    }

    public void deleteLabel(@NotNull Long id) {
        if (!repo.deleteById(id)) {
            throw new AppException("Label not found for deletion", 40403);
        }
    }

    private Label toEntity(LabelRequestTo dto) {
        Label label = new Label();
        label.setId(dto.id());
        label.setName(dto.name());
        return label;
    }

    private LabelResponseTo toResponse(Label label) {
        return new LabelResponseTo(label.getId(), label.getName());
    }
}