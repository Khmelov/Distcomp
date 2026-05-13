package com.bsuir.distcomp.service;

import com.bsuir.distcomp.dto.MarkerRequestTo;
import com.bsuir.distcomp.dto.MarkerResponseTo;
import com.bsuir.distcomp.entity.Marker;
import com.bsuir.distcomp.exception.EntityNotFoundException;
import com.bsuir.distcomp.mapper.MarkerMapper;
import com.bsuir.distcomp.repository.MarkerRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class MarkerService implements CrudService<MarkerRequestTo, MarkerResponseTo>{

    private final MarkerRepository repository;
    private final MarkerMapper mapper;

    public MarkerResponseTo create(MarkerRequestTo dto) {

        Marker marker = mapper.toEntity(dto);
        Marker saved = repository.save(marker);

        return mapper.toDto(saved);
    }

    public List<MarkerResponseTo> getAll() {

        return repository.findAll()
                .stream()
                .map(mapper::toDto)
                .toList();
    }

    public MarkerResponseTo getById(Long id) {

        Optional<Marker> marker = repository.findById(id);

        if (marker.isEmpty()) {
            throw new EntityNotFoundException("Marker not found with id " + id);
        }

        return mapper.toDto(marker.get());
    }

    public MarkerResponseTo update(Long id, MarkerRequestTo dto) {

        Marker existing = repository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Marker not found with id " + id));

        existing.setName(dto.getName());

        return mapper.toDto(repository.save(existing));
    }

    public void delete(Long id) {
        if (repository.findById(id).isEmpty()) {
            throw new EntityNotFoundException("Marker not found with id " + id);
        }
        repository.deleteById(id);
    }

}

