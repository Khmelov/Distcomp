package com.example.distcomp_1.controller;

import com.example.distcomp_1.mapper.MarkerDto;
import com.example.distcomp_1.mdoel.Author;
import com.example.distcomp_1.mdoel.Marker;
import com.example.distcomp_1.repository.MarkerRepository;
import com.example.distcomp_1.service.MarkerService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/v1.0")
public class MarkerController {

    private final MarkerDto markerDto;
    private final MarkerRepository markerRepository;
    private final MarkerService markerService;

    @Autowired
    public MarkerController(MarkerDto markerDto, MarkerRepository markerRepository, MarkerService markerService) {
        this.markerDto = markerDto;
        this.markerRepository = markerRepository;
        this.markerService = markerService;
    }
    @GetMapping("/markers")
    @ResponseStatus(HttpStatus.OK)
    public List<Marker.Out> getMarkers() {
        return markerRepository.getMarkers()
                .stream()
                .map(markerDto::Out)
                .collect(Collectors.toList());
    }

    @PostMapping("/markers")
    @ResponseStatus(HttpStatus.CREATED)
    public Marker.Out createMarker(@RequestBody @Valid Marker.In inputDto) {
        Marker entity = markerDto.In(inputDto);
        Marker saved = markerService.save(entity);
        return markerDto.Out(saved);
    }

    @GetMapping("/markers/{id}")
    public Marker.Out getMarkerById(@PathVariable Long id) {
        return markerDto.Out(markerService.findById(id));
    }

    @PutMapping("/markers")
    public ResponseEntity<Marker.Out> updateMarker(@RequestBody @Valid Marker.In in) {
        Marker marker = markerService.findById(in.getId());
        if (marker == null) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        } else {
            Marker newAuthor = markerDto.In(in);
            Marker updated = markerService.update(newAuthor);
            return ResponseEntity.ok(markerDto.Out(updated));
        }
    }

    @DeleteMapping("/markers/{id}")
    public ResponseEntity<Void> deleteMarker(@PathVariable Long id) {
        if (markerService.findById(id) != null) {
            markerService.deleteById(id);
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.notFound().build();
    }
}
