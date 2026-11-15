package com.example.task320.service;

import com.example.task320.dto.MarkerRequestTo;
import com.example.task320.dto.MarkerResponseTo;
import com.example.task320.entity.Marker;
import com.example.task320.repository.MarkerRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

// Сервисный слой содержит бизнес-логику работы с маркерами.
// Взаимодействует с MarkerRepository и преобразует сущности в DTO.
@Service
public class MarkerService implements BaseService<Marker, Long, MarkerResponseTo> {

    // Репозиторий отвечает за хранение, поиск и удаление маркеров.
    private final MarkerRepository markerRepository;

    // Spring передаёт репозиторий через конструктор.
    public MarkerService(MarkerRepository markerRepository) {
        this.markerRepository = markerRepository;
    }

    // Получение списка всех маркеров без пагинации.
    public List<MarkerResponseTo> findAll() {
        return markerRepository.findAll()
                .stream()
                .map(this::toResponse)
                .toList();
    }

    // Получение маркеров с учётом пагинации.
    @Override
    public Page<MarkerResponseTo> findAll(Pageable pageable) {
        return markerRepository.findAll(pageable)
                .map(this::toResponse);
    }

    // Получение маркеров с учётом сортировки.
    @Override
    public List<MarkerResponseTo> findAll(Sort sort) {
        return markerRepository.findAll(sort)
                .stream()
                .map(this::toResponse)
                .toList();
    }

    // Получение маркеров по условиям фильтрации с пагинацией.
    @Override
    public Page<MarkerResponseTo> findAll(
            Specification<Marker> specification,
            Pageable pageable) {
        return markerRepository.findAll(specification, pageable)
                .map(this::toResponse);
    }

    // Поиск маркера по идентификатору.
    @Override
    public Optional<MarkerResponseTo> findById(Long id) {
        return markerRepository.findById(id)
                .map(this::toResponse);
    }

    // Поиск маркера по названию.
    public Optional<MarkerResponseTo> findByName(String name) {
        return markerRepository.findByName(name)
                .map(this::toResponse);
    }

    // Создание нового маркера.
    public MarkerResponseTo save(MarkerRequestTo request) {
        Marker marker = new Marker();
        marker.setName(request.getName());

        Marker savedMarker = markerRepository.save(marker);

        return toResponse(savedMarker);
    }

    // Обновление существующего маркера.
    // Если запись отсутствует, возвращается Optional.empty().
    public Optional<MarkerResponseTo> update(Long id, MarkerRequestTo request) {
        Optional<Marker> existingMarker = markerRepository.findById(id);

        if (existingMarker.isEmpty()) {
            return Optional.empty();
        }

        Marker marker = existingMarker.get();
        marker.setName(request.getName());

        Marker updatedMarker = markerRepository.save(marker);

        return Optional.of(toResponse(updatedMarker));
    }

    // Удаление маркера по идентификатору.
    public boolean deleteById(Long id) {
        if (!markerRepository.existsById(id)) {
            return false;
        }

        markerRepository.deleteById(id);
        return true;
    }

    // Преобразование сущности Marker в DTO ответа.
    private MarkerResponseTo toResponse(Marker marker) {
        return new MarkerResponseTo(
                marker.getId(),
                marker.getName()
        );
    }
}