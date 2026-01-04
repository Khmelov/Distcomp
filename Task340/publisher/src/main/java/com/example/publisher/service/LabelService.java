package com.example.publisher.service;

import com.example.publisher.dto.LabelRequestTo;
import com.example.publisher.dto.LabelResponseTo;
import com.example.publisher.exception.AppException;
import com.example.publisher.model.Label;
import com.example.publisher.repository.LabelRepository;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional
public class LabelService {
    private final LabelRepository repository;

    public LabelService(LabelRepository repository) {
        this.repository = repository;
    }

    @Transactional(readOnly = true)
    public List<LabelResponseTo> getAllLabels() {
        return repository.findAll().stream()
                .map(this::toResponse)
                .toList();
    }

    @Transactional(readOnly = true)
    public LabelResponseTo getLabelById(@NotNull Long id) {
        return repository.findById(id)
                .map(this::toResponse)
                .orElseThrow(() -> new AppException("Label not found", 40403));
    }

    public LabelResponseTo createLabel(@Valid LabelRequestTo request) {
        if (repository.findByName(request.name()).isPresent()) {
            throw new AppException("Label name already exists", 40904);
        }
        Label label = new Label(request.name());
        Label saved = repository.save(label);
        return toResponse(saved);
    }

    public LabelResponseTo updateLabel(@Valid LabelRequestTo request) {
        if (request.id() == null) {
            throw new AppException("ID required for update", 40003);
        }

        Label existing = repository.findById(request.id())
                .orElseThrow(() -> new AppException("Label not found for update", 40403));

        if (!existing.getName().equals(request.name()) &&
                repository.findByName(request.name()).isPresent()) {
            throw new AppException("Label name already exists", 40904);
        }

        existing.setName(request.name());
        Label updated = repository.save(existing);
        return toResponse(updated);
    }

    public void deleteLabel(@NotNull Long id) {
        if (!repository.existsById(id)) {
            throw new AppException("Label not found for deletion", 40403);
        }
        repository.deleteById(id);
    }

    private LabelResponseTo toResponse(Label label) {
        return new LabelResponseTo(label.getId(), label.getName());
    }
}