package com.example.Task310.service;

import com.example.Task310.bean.*;
import com.example.Task310.dto.*;
import com.example.Task310.exception.*;
import com.example.Task310.mapper.StoryMapper;
import com.example.Task310.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;



@Service
@RequiredArgsConstructor
public class StoryService {
    private final StoryRepository repository;
    private final EditorRepository editorRepository;
    private final MarkerRepository markerRepository;
    private final StoryMapper mapper;

    @Transactional
    public StoryResponseTo create(StoryRequestTo request) {
        if (repository.existsByTitle(request.getTitle())) {
            throw new AlreadyExistsException("Story title already exists");
        }
        Editor editor = editorRepository.findById(request.getEditorId())
                .orElseThrow(() -> new ResourceNotFoundException("Editor association not found"));

        Story story = mapper.toEntity(request);
        story.setEditor(editor);
        story.setCreated(LocalDateTime.now());
        story.setModified(LocalDateTime.now());

        // Обработка маркеров из списка строк
        processMarkers(request.getMarkers(), story);

        return mapper.toResponse(repository.save(story));
    }

    @Transactional
    public StoryResponseTo update(Long id, StoryRequestTo request) {
        Story existing = repository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Story not found"));

        if (!existing.getTitle().equals(request.getTitle()) && repository.existsByTitle(request.getTitle())) {
            throw new AlreadyExistsException("Title already taken");
        }

        Editor editor = editorRepository.findById(request.getEditorId())
                .orElseThrow(() -> new ResourceNotFoundException("Editor association not found"));

        mapper.updateEntity(request, existing);
        existing.setEditor(editor);
        existing.setModified(LocalDateTime.now());

        processMarkers(request.getMarkers(), existing);

        return mapper.toResponse(repository.save(existing));
    }

    private void processMarkers(List<String> markerNames, Story story) {
        if (markerNames != null) {
            Set<Marker> markers = markerNames.stream()
                    .map(name -> markerRepository.findByName(name)
                            .orElseGet(() -> markerRepository.save(Marker.builder().name(name).build())))
                    .collect(Collectors.toSet());
            story.setMarkers(markers);
        }
    }

    public List<StoryResponseTo> findAll(Pageable pageable) {
        return repository.findAll(pageable).stream().map(mapper::toResponse).toList();
    }

    public StoryResponseTo findById(Long id) {
        return repository.findById(id).map(mapper::toResponse)
                .orElseThrow(() -> new ResourceNotFoundException("Story not found"));
    }

    @Transactional
    public void delete(Long id) {
        if (!repository.existsById(id)) throw new ResourceNotFoundException("Story not found");
        repository.deleteById(id);
    }
}